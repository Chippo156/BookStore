package com.book.book_store.controller;

import com.book.book_store.dto.request.UserCreationRequest;
import com.book.book_store.dto.response.ResponseData;
import com.book.book_store.dto.response.UserCreationResponse;
import com.book.book_store.dto.response.UserDetailResponse;
import com.book.book_store.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor

@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserController {

    private final UserService userService;

    @PostMapping("/users-creation")
    ResponseData<UserCreationResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Create user controller layer");
        return ResponseData.<UserCreationResponse>builder()
                .data(userService.createUser(request))
                .code(200)
                .message("User created")
                .build();
    }
    @GetMapping("/get-all-users")
    ResponseData<List<UserDetailResponse>> getAllUsers(){
        return ResponseData.<List<UserDetailResponse>>builder()
                .code(200)
                .message("All users")
                .data(userService.getAllUsers())
                .build();
    }

}
