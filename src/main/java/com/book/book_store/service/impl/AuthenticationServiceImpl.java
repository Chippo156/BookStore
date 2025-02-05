package com.book.book_store.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.book.book_store.dto.request.LogoutRequest;
import com.book.book_store.dto.request.SignInRequest;
import com.book.book_store.dto.response.RefreshTokenResponse;
import com.book.book_store.dto.response.SignInResponse;
import com.book.book_store.exception.AppException;
import com.book.book_store.exception.ErrorCode;
import com.book.book_store.model.User;
import com.book.book_store.repository.UserRepository;
import com.book.book_store.service.AuthenticationService;
import com.book.book_store.service.JwtService;
import com.book.book_store.service.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    @Override
    @Transactional
    public SignInResponse signIn(SignInRequest request, HttpServletResponse response) {
        log.info("Authentication Start");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        User user = (User) authentication.getPrincipal();
        log.info("Authority : {} ", user.getAuthorities());
        final String accessToken  = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        response.addCookie(cookie);
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        log.info("Refresh token start");
        if(StringUtils.isBlank(refreshToken)){
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        String email = jwtService.extractByUserName(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXCITED));
        if(!Objects.equals(refreshToken,user.getRefreshToken()) || StringUtils.isBlank(user.getRefreshToken())){
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        try{
            boolean isValidToken = jwtService.verificationToken(refreshToken,user);
            if(!isValidToken){
                throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
            String accessToken = jwtService.generateAccessToken(user);
            log.info("refresh token success");

            return RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .userId(user.getId())
                    .build();
        }catch (ParseException | JOSEException e) {
            log.error("Error while refresh token");
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

    }

    @Override
    public void Logout(LogoutRequest request,HttpServletResponse response) throws ParseException {
        log.info("Logout start");
        String email = jwtService.extractByUserName(request.getAccessToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXCITED));
        long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        log.info("Access token expired : {}",accessTokenExp);
        if(accessTokenExp > 0){
            try{
                String jwtId = SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
                redisService.save(jwtId,request.getAccessToken(),accessTokenExp, TimeUnit.MILLISECONDS);
                log.info("Access token added to blacklist : {}",redisService.get(jwtId));
                user.setRefreshToken(null);
                userRepository.save(user);
                deleteRefreshTokenCookie(response);
            }catch (ParseException e){
                throw new AppException(ErrorCode.SIGN_OUT_FAILED);
            }
        }
    }
    private void deleteRefreshTokenCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken",null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
