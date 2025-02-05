package com.book.book_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {
    @NotBlank(message = "Token cannot be null")
    private String accessToken;
}
