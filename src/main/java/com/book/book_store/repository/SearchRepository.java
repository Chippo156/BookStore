package com.book.book_store.repository;

import com.book.book_store.dto.response.BookDetailResponse;
import com.book.book_store.dto.response.PageResponse;
import com.book.book_store.mapper.BookMapper;
import com.book.book_store.model.Book;
import com.book.book_store.model.User;
import com.book.book_store.repository.criteria.SearchCriteria;
import com.book.book_store.repository.criteria.SearchCriteriaQueryConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> bookCriteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = bookCriteriaQuery.from(Book.class);
        Predicate predicate = criteriaBuilder.conjunction();

        List<SearchCriteria> criteriaList = new ArrayList<>();
        if (search != null) {
            for (String s : search) {
                Pattern pattern = Pattern.compile("(\\w+?)([:<>!])(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(criteriaBuilder, root, predicate);
        if (!criteriaList.isEmpty()) {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            bookCriteriaQuery.where(predicate);
        }
        if (StringUtils.hasLength(user)) {
            log.info("Sort book and join User");
            Join<Book, User> userJoin = root.join("author");
            Predicate likeToFullName = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", user));
            Predicate likeToEmail = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", user));
            Predicate finalPredicate = criteriaBuilder.or(likeToFullName, likeToEmail);
            bookCriteriaQuery.where(predicate, finalPredicate);
        }
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)([:><!])(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    bookCriteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
                } else {
                    bookCriteriaQuery.orderBy(criteriaBuilder.desc(root.get(columnName)));
                }
            }
        }
        List<Book> bookList = entityManager.createQuery(bookCriteriaQuery)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        Long totalElements = getTotalElements(criteriaList, user);

        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .totalElement(totalElements)
                .data(BookMapper.bookDetailResponses(bookList))
                .build();

    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, String user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = query.from(Book.class);
        // Khởi tạo Predicate ban đầu là TRUE
        Predicate predicate = criteriaBuilder.conjunction();

        if (!criteriaList.isEmpty()) {
            SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(criteriaBuilder, root, predicate);
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
        }

        if (StringUtils.hasLength(user)) {
            Join<Book, User> userJoin = root.join("author");
            Predicate likeToFullName = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", user));
            Predicate likeToEmail = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", user));
            Predicate finalPre = criteriaBuilder.or(likeToFullName, likeToEmail);
            query.where(predicate, finalPre);
        }
        query.select(criteriaBuilder.count(root)).where(predicate);

        return entityManager.createQuery(query)
                .getSingleResult();
    }

    public PageResponse<BookDetailResponse> getBookWithSortAndKeyword(int page, int size, String sort, String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = criteriaQuery.from(Book.class);
        var listPredicate = new ArrayList<Predicate>();
        if (StringUtils.hasLength(keyword)) {
            Predicate toTitle = criteriaBuilder.like(root.get("title"), String.format("%%%s%%", keyword));
            Predicate toLanguage = criteriaBuilder.like(root.get("language"), String.format("%%%s%%", keyword));
            Predicate toIsbn = criteriaBuilder.like(root.get("isbn"), String.format("%%%s%%", keyword));
            Predicate toDescription = criteriaBuilder.like(root.get("description"), String.format("%%%s%%", keyword));
//            Predicate toPrice = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), keyword);

            listPredicate.add(toTitle);
            listPredicate.add(toLanguage);
            listPredicate.add(toIsbn);
            listPredicate.add(toDescription);
//            listPredicate.add(toPrice);

            Join<Book, User> userJoin = root.join("author", JoinType.LEFT);
            Predicate toFullName = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", keyword));
            Predicate toEmail = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", keyword));
            listPredicate.add(toFullName);
            listPredicate.add(toEmail);
        }
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile(("(\\w+?)([:><!])(asc|desc)"));
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(columnName)));
                }
            }
        }
        Predicate predicate = listPredicate.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.or(listPredicate.toArray(new Predicate[0]));
        criteriaQuery.where(predicate);
        List<Book> bookList = entityManager.createQuery(criteriaQuery)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
        Long totalElement = getTotalElement(keyword);
        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElement(totalElement)
                .totalPages((int) Math.ceil((double) totalElement / size))
                .data(BookMapper.bookDetailResponses(bookList))
                .build();
    }
    private Long getTotalElement(String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        List<Predicate> listPredicate = new ArrayList<>();
        if(StringUtils.hasLength(keyword)) {
            Predicate toTitle = criteriaBuilder.like(root.get("title"), String.format("%%%s%%", keyword));
            Predicate toLanguage = criteriaBuilder.like(root.get("language"), String.format("%%%s%%", keyword));
            Predicate toPrice = criteriaBuilder.like(root.get("description"), String.format("%%%s%%", keyword));
            listPredicate.add(toTitle);
            listPredicate.add(toLanguage);
            listPredicate.add(toPrice);

            Join<Book, User> userJoin = root.join("author", JoinType.LEFT);
            Predicate toFullNamePredicate = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", keyword));
            Predicate toEmailPredicate = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", keyword));
            listPredicate.add(toFullNamePredicate);
            listPredicate.add(toEmailPredicate);
        }
        Predicate predicate = listPredicate.isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.or(listPredicate.toArray(new Predicate[0]));
        criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);

        return entityManager.createQuery(criteriaQuery)
                .getSingleResult();
    }


}
