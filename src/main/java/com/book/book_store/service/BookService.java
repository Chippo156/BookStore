package com.book.book_store.service;

import com.book.book_store.dto.request.BookCreationRequest;
import com.book.book_store.dto.response.BookCreationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookCreationResponse uploadBook(BookCreationRequest request, MultipartFile thumbnail, MultipartFile bookPath);
    BookCreationResponse getBookById(Long id);
}
