package com.book.book_store.controller;

import com.book.book_store.dto.request.BookCreationRequest;
import com.book.book_store.dto.response.BookCreationResponse;
import com.book.book_store.dto.response.BookDetailResponse;
import com.book.book_store.dto.response.PageResponse;
import com.book.book_store.dto.response.ResponseData;
import com.book.book_store.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    @PostMapping("/upload-book")
    public ResponseData<BookCreationResponse> uploadBook(
            @RequestPart(name = "request") BookCreationRequest request,
            @RequestPart(name = "thumbnail") MultipartFile thumbnail,
            @RequestPart(name = "book-pdf", required = false) MultipartFile bookPath
    ) {
        log.info("Uploading book");
        var result = bookService.uploadBook(request, thumbnail, bookPath);

        return ResponseData.<BookCreationResponse>builder()
                .data(result)
                .message("Book uploaded successfully")
                .code(HttpStatus.CREATED.value())
                .build();
    }
    @GetMapping("/get-book/{id}")
    public ResponseData<BookDetailResponse> getBook(@PathVariable Long id) {
        log.info("Getting book with id {}", id);
        var result = bookService.getBookById(id);

        return ResponseData.<BookDetailResponse>builder()
                .data(result)
                .message("Book retrieved successfully")
                .code(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/books-search-criteria")
    ResponseData<PageResponse<BookDetailResponse>> getAllBookAndSearchCriteria(
            @RequestParam(name ="page",required = false, defaultValue = "1") int page,
            @RequestParam(name = "size",required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort",required = false) String sortBy,
            @RequestParam(name = "user", required = false) String user,
            @RequestParam(name = "search", required = false) String... search

    )
    {
        var result = bookService.getBookWithSortMultiFieldAndSearch(page, size, sortBy, user, search);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .data(result)
                .message("Get all book with search criteria")
                .code(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/books-search-keyword")
    ResponseData<PageResponse<BookDetailResponse>> getAllBookAndSearchKeyword(
            @RequestParam(name ="page",required = false, defaultValue = "1") int page,
            @RequestParam(name = "size",required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort",required = false) String sortBy,
            @RequestParam(name = "keyword", required = false) String keyword
    )
    {
        var result = bookService.getBookWithSortAndKeyword(page, size, sortBy, keyword);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .data(result)
                .message("Get all book with search keyword")
                .code(HttpStatus.OK.value())
                .build();
    }
}
