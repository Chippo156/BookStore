package com.book.book_store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ErrorResponse implements Serializable {
    private Date timestamp;
    private int status;
    private String error;
    private String path;
}
