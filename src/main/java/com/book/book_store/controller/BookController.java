package com.book.book_store.controller;

import com.book.book_store.dto.request.BookCreationRequest;
import com.book.book_store.dto.response.BookCreationResponse;
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
}
