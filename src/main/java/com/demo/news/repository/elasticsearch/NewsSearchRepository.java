package com.demo.news.repository.elasticsearch;

import com.demo.news.document.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsSearchRepository extends ElasticsearchRepository<NewsDocument, String> {
    List<NewsDocument> findByTitleContaining(String title);
    List<NewsDocument> findByContentContaining(String content);
    List<NewsDocument> findByCategory(String category);
    List<NewsDocument> findByAuthor(String author);
}
