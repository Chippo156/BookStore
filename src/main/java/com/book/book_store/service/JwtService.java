package com.book.book_store.service;

import com.book.book_store.model.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean verificationToken(String token, User user);
    long extractTokenExpired(String token);
}

