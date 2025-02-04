package com.book.book_store.dto.response;

import com.book.book_store.common.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailResponse {

    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private Integer age;
    private Gender gender;
    private String avatarUrl;
}
