package com.book.book_store.service.impl;

import com.book.book_store.exception.AppException;
import com.book.book_store.exception.ErrorCode;
import com.book.book_store.model.User;
import com.book.book_store.model.UserHasRole;
import com.book.book_store.service.JwtService;
import com.book.book_store.service.RedisService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final RedisService redisService;

    @Override
    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(60, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("Authority", buildAuthority(user))
                .claim("Permission", buildPermission(user))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }


    @Override
    public String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(14, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        var payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        return jwsObject.serialize();
    }

    @Override
    public boolean verificationToken(String token, User user) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(StringUtils.isNotBlank(redisService.get(jwtId))) {
            throw new AppException(ErrorCode.TOKEN_BLACK_LISTED);
        }
        var email = signedJWT.getJWTClaimsSet().getSubject();
        var expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!Objects.equals(user.getEmail(), email)) {
            log.error("Email in token not match email system");
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        if (expiration.before(new Date())) {
            log.error("Token expired");
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        return signedJWT.verify(new MACVerifier(secretKey));
    }

    @Override
    public long extractTokenExpired(String token) {
        try{
            long expirationTime = SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime-currentTime,0);
        }catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public String extractByUserName(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.USER_NOT_EXCITED);
        }
    }

    private String buildAuthority(User user) {
        return user.getUserHasRoles().stream().map(u -> u.getRole().getName()).collect(Collectors.joining(", "));
    }

    private String buildPermission(User user) {
        StringJoiner joiner = new StringJoiner(", ");
        Optional.ofNullable(user.getUserHasRoles())
                .ifPresent(userHasRoles -> userHasRoles.stream().map(UserHasRole::getRole)
                        .flatMap(role -> role.getRoleHasPermissions().stream().map(roleHasPermission -> roleHasPermission.getRole().getName()))
                        .distinct()
                        .forEach(joiner::add));

        return joiner.toString();
    }

}
