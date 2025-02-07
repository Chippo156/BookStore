package com.book.book_store.service;

import com.book.book_store.dto.request.BookCreationRequest;
import com.book.book_store.dto.response.BookCreationResponse;
import com.book.book_store.dto.response.BookDetailResponse;
import com.book.book_store.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookCreationResponse uploadBook(BookCreationRequest request, MultipartFile thumbnail, MultipartFile bookPath);
    BookDetailResponse getBookById(Long id);
    PageResponse<BookDetailResponse> getAllBook(int page, int size);

    PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search);
    PageResponse<BookDetailResponse> getBookWithSortAndKeyword(int page, int size, String sortBy, String keyword);
}
