package com.book.book_store.repository;

import com.book.book_store.model.BookElasticSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookElasticRepository extends ElasticsearchRepository<BookElasticSearch, String> {
}
