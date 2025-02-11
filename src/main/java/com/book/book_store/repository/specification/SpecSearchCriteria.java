package com.book.book_store.repository.specification;


import com.book.book_store.common.SearchOperation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpecSearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String key, String operation, Object value, String prefix, String suffix) {
        SearchOperation searchOperation = SearchOperation.getOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == SearchOperation.EQUALITY) {
                boolean startWith = prefix != null && prefix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
                boolean endWith = suffix != null && suffix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
                if (startWith && endWith) {
                    this.operation = SearchOperation.CONTAINS;
                } else if (startWith) {
                    this.operation = SearchOperation.ENDS_WITH;
                } else {
                    this.operation = SearchOperation.STARTS_WITH;

                }
            }
        }
        this.key = key;
        this.value = value;
        this.operation = searchOperation;
    }
}
