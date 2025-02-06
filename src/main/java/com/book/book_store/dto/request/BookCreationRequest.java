package com.book.book_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
public class BookCreationRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "ISBN cannot be blank")
    private String isbn;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Price cannot be blank")
    private BigDecimal price;

    @NotBlank(message = "Language cannot be blank")
    private String language;
}
