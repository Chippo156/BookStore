package com.book.book_store.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
public class PageResponse<T> implements Serializable {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private Long totalElement;

    @Builder.Default
    private List<T> data = Collections.emptyList();
}
