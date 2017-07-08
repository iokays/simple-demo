package com.iokays.elasticsearch.repositories.book;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.iokays.elasticsearch.entities.Book;

public interface SampleElasticSearchBookRepository extends ElasticsearchRepository<Book, String> {

}
