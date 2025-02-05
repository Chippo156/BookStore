package com.book.book_store.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RefreshTokenResponse {
    private Long userId;
    private String accessToken;
}
