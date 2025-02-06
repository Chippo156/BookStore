package com.book.book_store.service;

import com.book.book_store.model.BookElasticSearch;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaService {
    void saveToElasticSearch(BookElasticSearch bookElasticSearch, Acknowledgment acknowledgement);
}
