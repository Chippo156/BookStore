package com.book.book_store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_EXCITED(400,"User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXCITED(404,"User not exists", HttpStatus.BAD_REQUEST);



    private final int code;
    private final String  message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
