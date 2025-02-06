package com.book.book_store.service.impl;

import co.elastic.clients.elasticsearch.license.post.Acknowledgement;
import com.book.book_store.model.BookElasticSearch;
import com.book.book_store.repository.BookElasticRepository;
import com.book.book_store.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final BookElasticRepository repository;
    @Override
    @KafkaListener(topics = "save-to-elastic-search", groupId = "book-elastic-search")
    public void saveToElasticSearch(BookElasticSearch bookElasticSearch, Acknowledgment acknowledgement) {
        try{
            log.info("Saving book to elastic search");
            if(bookElasticSearch != null && !repository.existsById(bookElasticSearch.getId())){
                repository.save(bookElasticSearch);
                log.info("Book saved to elastic search");
                acknowledgement.acknowledge();
            }else{
                log.error("Book not saved to elastic search");
            }
        }
        catch (Exception e){
            log.error("Error saving book to elastic search", e);
            throw e;
        }
    }
}
