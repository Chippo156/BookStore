package com.book.book_store.service;

import com.book.book_store.model.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean verificationToken(String token, User user) throws ParseException, JOSEException;
    long extractTokenExpired(String token);
    String extractByUserName(String accessToken);
}

