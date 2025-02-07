package com.book.book_store.service.impl;

import com.book.book_store.dto.request.BookCreationRequest;
import com.book.book_store.dto.response.BookCreationResponse;
import com.book.book_store.dto.response.BookDetailResponse;
import com.book.book_store.dto.response.PageResponse;
import com.book.book_store.exception.AppException;
import com.book.book_store.exception.ErrorCode;
import com.book.book_store.mapper.BookMapper;
import com.book.book_store.model.Book;
import com.book.book_store.model.BookElasticSearch;
import com.book.book_store.model.User;
import com.book.book_store.repository.BookRepository;
import com.book.book_store.repository.SearchRepository;
import com.book.book_store.repository.UserRepository;
import com.book.book_store.service.BookService;
import com.book.book_store.service.CloudinaryService;
import com.book.book_store.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SearchRepository searchRepository;

    @Override
    @PreAuthorize("isAuthenticated()")
    public BookCreationResponse uploadBook(BookCreationRequest request, MultipartFile thumbnail, MultipartFile bookPath) {
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXCITED));
        log.info("User {} is uploading book", user.getEmail());

        String thumbnailPath = cloudinaryService.uploadImage(thumbnail);
        String bookPath1 = null;
        if (bookPath != null) {
            bookPath1 = cloudinaryService.uploadImage(bookPath);
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(user)
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .price(request.getPrice())
                .language(request.getLanguage())
                .thumbnail(thumbnailPath)
                .bookPath(bookPath1)
                .publisher(user.getEmail())
                .build();
        bookRepository.save(book);

        BookElasticSearch bookElasticSearch = BookElasticSearch
                .builder()
                .id(book.getId().toString())
                .title(book.getTitle())
                .authorName(book.getAuthor().getFullName())
                .price(book.getPrice())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .language(book.getLanguage())
                .build();

        log.info("Sending book to elastic search");
        kafkaTemplate.send("save-to-elastic-search", bookElasticSearch);
        return BookCreationResponse.builder()
                .authorName(user.getFullName())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .language(book.getLanguage())
                .price(book.getPrice())
                .thumbnail(book.getThumbnail())
                .title(book.getTitle())
                .bookPath(book.getBookPath())
                .build();
    }

    @Override
    public BookDetailResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return BookDetailResponse
                .builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .price(book.getPrice())
                .language(book.getLanguage())
                .thumbnail(book.getThumbnail())
                .bookPath(book.getBookPath())
                .authorName(book.getAuthor().getFullName())
                .build();
    }

    @Override
    public PageResponse<BookDetailResponse> getAllBook(int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        List<Book> books = bookPage.getContent();
        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalElement(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .data(BookMapper.bookDetailResponses(books))
                .build();
    }

    @Override
    public PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search) {
        return searchRepository.getBookWithSortMultiFieldAndSearch(page, size, sortBy, user, search);
    }

    @Override
    public PageResponse<BookDetailResponse> getBookWithSortAndKeyword(int page, int size, String sortBy, String keyword) {
        return searchRepository.getBookWithSortAndKeyword(page, size, sortBy, keyword);
    }
}