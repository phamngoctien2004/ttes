package com.demo.news.repository.mysql;

import com.demo.news.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findByCategory(String category);
    List<NewsEntity> findByAuthor(String author);
}
