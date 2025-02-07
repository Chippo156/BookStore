package com.book.book_store.mapper;


import com.book.book_store.dto.response.BookDetailResponse;
import com.book.book_store.model.Book;

import java.util.List;

public class BookMapper {
    private BookMapper(){

    }
    public static List<BookDetailResponse> bookDetailResponses(List<Book> books){
        return books.stream().map(book -> BookDetailResponse
                .builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .authorName(book.getAuthor().getFullName())
                .price(book.getPrice())
                .description(book.getDescription())
                .language(book.getLanguage())
                .thumbnail(book.getThumbnail())
                .bookPath(book.getBookPath())
                .build()).toList();
    }
}
