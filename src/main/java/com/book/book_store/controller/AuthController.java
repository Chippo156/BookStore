package com.book.book_store.controller;

import com.book.book_store.dto.request.LogoutRequest;
import com.book.book_store.dto.request.SignInRequest;
import com.book.book_store.dto.response.RefreshTokenResponse;
import com.book.book_store.dto.response.ResponseData;
import com.book.book_store.dto.response.SignInResponse;
import com.book.book_store.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    ResponseData<SignInResponse> signIn(@RequestBody SignInRequest request , HttpServletResponse response){
        var result = authenticationService.signIn(request, response);
        return ResponseData.<SignInResponse>builder()
                .data(result)
                .code(HttpStatus.OK.value())
                .message("Sign in success")
                .build();
    }
    @PostMapping("/refresh-token")
    ResponseData<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken){
        var result = authenticationService.refreshToken(refreshToken);
        return ResponseData.<RefreshTokenResponse>builder()
                .data(result)
                .code(HttpStatus.OK.value())
                .message("Refresh token success")
                .build();
    }
    @PostMapping("/logout")
    ResponseData<Void> logout (@RequestBody @Valid LogoutRequest refreshToken, HttpServletResponse response) throws ParseException {
        authenticationService.Logout(refreshToken, response);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Logout success")
                .build();
    }

}