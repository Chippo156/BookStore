package com.book.book_store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserCreationRequest {

        @NotBlank(message = "First name can't be blank")
        private String firstName;
        @NotBlank(message = "Last name can't be blank")
        private String lastName;
        @NotBlank(message = "Email can't be blank")
        private String email;
        @NotBlank(message = "Password can't be blank")
        private String password;

        @NotNull(message = "Date of birth can't be null")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateOfBirth;

}
