package com.book.book_store.service.impl;

import com.book.book_store.common.UserType;
import com.book.book_store.dto.request.UserCreationRequest;
import com.book.book_store.dto.response.UserCreationResponse;
import com.book.book_store.dto.response.UserDetailResponse;
import com.book.book_store.exception.AppException;
import com.book.book_store.exception.ErrorCode;
import com.book.book_store.model.Role;
import com.book.book_store.model.User;
import com.book.book_store.model.UserHasRole;
import com.book.book_store.repository.RoleRepository;
import com.book.book_store.repository.UserRepository;
import com.book.book_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createUser(UserCreationRequest request) {
        log.info("User create");
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXCITED);
        }
        Role role = roleRepository.findByName(String.valueOf(UserType.USER)).orElseThrow(()->new RuntimeException("Role not found"));
        User user = User.builder()
                .email(request.getEmail())
                .fullName(String.format("%s %s",request.getFirstName(),request.getLastName()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user.setCreateBy(user.getEmail());

        UserHasRole userHasRole = UserHasRole.builder()
                .role(role)
                .user(user)
                .build();
        user.setUserHasRoles(Set.of(userHasRole));
        userRepository.save(user);

        log.info("User created");

        return UserCreationResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public List<UserDetailResponse> getAllUsers() {
        log.info("Get all users");
        List<User> users = userRepository.findAll();
        return userRepository.findAll().stream().map(user -> UserDetailResponse.builder()
                .fullName(user.getFullName())
                .age(user.getAge())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhoneNumber())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .build()).toList();

    }

}
