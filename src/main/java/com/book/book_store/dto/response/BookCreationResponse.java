package com.book.book_store.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


@Setter
@Getter
@Builder
public class BookCreationResponse implements Serializable {
    private String authorName;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal price;
    private String language;
    private String thumbnail;
    private String bookPath;


}
