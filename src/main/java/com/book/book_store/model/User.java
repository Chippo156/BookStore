package com.book.book_store.model;

import com.book.book_store.common.Gender;
import com.book.book_store.common.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class User extends AbstractEntity<Long>{

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "first_name" ,nullable = false)
    private String firstName;

    @Column(name = "last_name" ,nullable = false)
    private String lastName;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "refresh_token",columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "age")
    private Integer age;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated
    @Column(name = "gender")
    private Gender gender;

    @Enumerated
    @Column(name = "user_status")
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<UserHasRole> userHasRoles;


}

