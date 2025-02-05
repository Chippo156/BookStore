package com.book.book_store.service;

import com.book.book_store.dto.request.LogoutRequest;
import com.book.book_store.dto.request.SignInRequest;
import com.book.book_store.dto.response.RefreshTokenResponse;
import com.book.book_store.dto.response.SignInResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthenticationService {
     SignInResponse signIn(SignInRequest request, HttpServletResponse response);
     RefreshTokenResponse refreshToken(String refreshToken);
     void Logout(LogoutRequest request,HttpServletResponse response) throws ParseException;


}
