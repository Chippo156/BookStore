package com.book.book_store.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;

import java.util.function.Consumer;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaQueryConsumer implements Consumer<SearchCriteria> {

    private CriteriaBuilder criteriaBuilder;
    private Root<?> root;
    private Predicate predicate;

    @Override
    public void accept(SearchCriteria param) {
        switch (param.getOperation()) {
            case ":" -> {
                if (root.get(param.getKey()).getJavaType().equals(String.class)) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get(param.getKey()), String.format("%%%s%%", param.getValue()))
                    );
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(param.getKey()), param.getValue()));
                }
            }
            case "!" ->
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get(param.getKey()), param.getValue()));
            case ">" ->
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
            case "<" ->
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        }


    }
}
