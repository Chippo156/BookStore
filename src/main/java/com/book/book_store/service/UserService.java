package com.book.book_store.service;

import com.book.book_store.dto.request.UserCreationRequest;
import com.book.book_store.dto.response.UserCreationResponse;
import com.book.book_store.dto.response.UserDetailResponse;

import java.util.List;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
    List<UserDetailResponse> getAllUsers();

}
