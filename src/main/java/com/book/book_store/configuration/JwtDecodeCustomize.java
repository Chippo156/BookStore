package com.book.book_store.configuration;

import com.book.book_store.exception.AppException;
import com.book.book_store.exception.ErrorCode;
import com.book.book_store.model.User;
import com.book.book_store.repository.UserRepository;
import com.book.book_store.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtDecodeCustomize implements JwtDecoder {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private NimbusJwtDecoder nimbusJwtDecoder;
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), JWSAlgorithm.HS512.toString());
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        String email = jwtService.extractByUserName(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXCITED));
        try {
            boolean isValid = jwtService.verificationToken(token, user);
            if (isValid) {
                return nimbusJwtDecoder.decode(token);
            }
        } catch (ParseException | JOSEException e) {
            log.error("Jwt decoder: Token invalid");
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        throw new JwtException("Token invalid");
    }
}
