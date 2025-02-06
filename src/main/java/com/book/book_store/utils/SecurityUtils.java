package com.book.book_store.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class SecurityUtils {
    public SecurityUtils(){

    }
    public static Optional<String> getCurrentUserLogin(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }
    public static String extractPrincipal(Authentication authentication){
        if(authentication == null){
            return null;
        }
        if(authentication.getPrincipal() instanceof UserDetails userDetails){
            return userDetails.getUsername();
        }
        else if(authentication.getPrincipal() instanceof Jwt jwt){
            return jwt.getSubject();
        }
        else if(authentication.getPrincipal() instanceof String s){
            return s;
        }
        return null;

    }
}
