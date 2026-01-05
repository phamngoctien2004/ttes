package com.demo.news.controller;

import com.demo.news.dto.NewsDTO;
import com.demo.news.dto.SearchRequest;
import com.demo.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<NewsDTO> createNews(@RequestBody NewsDTO newsDTO) {
        log.info("POST /api/news - Creating news with title: {}", newsDTO.getTitle());
        NewsDTO created = newsService.createNews(newsDTO);
        log.info("POST /api/news - Successfully created news with id: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getAllNews() {
        log.info("GET /api/news - Fetching all news");
        List<NewsDTO> newsList = newsService.getAllNews();
        log.info("GET /api/news - Returned {} news items", newsList.size());
        return ResponseEntity.ok(newsList);
    }

    // Đặt /search và /sync TRƯỚC /{id} để tránh conflict routing
    @PostMapping("/search")
    public ResponseEntity<List<NewsDTO>> searchNews(@RequestBody SearchRequest request) {
        log.info("POST /api/news/search - Search request: keyword={}, category={}, author={}, page={}, size={}", 
                 request.getKeyword(), request.getCategory(), request.getAuthor(), 
                 request.getPage(), request.getSize());
        List<NewsDTO> results = newsService.searchNews(request);
        log.info("POST /api/news/search - Found {} results", results.size());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncToElasticsearch() {
        log.info("POST /api/news/sync - Starting sync to Elasticsearch");
        newsService.syncAllToElasticsearch();
        log.info("POST /api/news/sync - Sync completed successfully");
        return ResponseEntity.ok("Synced all news to Elasticsearch");
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable Long id) {
        log.info("GET /api/news/{} - Fetching news by id", id);
        NewsDTO news = newsService.getNewsById(id);
        log.info("GET /api/news/{} - Successfully retrieved news: {}", id, news.getTitle());
        return ResponseEntity.ok(news);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsDTO> updateNews(@PathVariable Long id, @RequestBody NewsDTO newsDTO) {
        log.info("PUT /api/news/{} - Updating news with title: {}", id, newsDTO.getTitle());
        NewsDTO updated = newsService.updateNews(id, newsDTO);
        log.info("PUT /api/news/{} - Successfully updated news", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        log.info("DELETE /api/news/{} - Deleting news", id);
        newsService.deleteNews(id);
        log.info("DELETE /api/news/{} - Successfully deleted news", id);
        return ResponseEntity.noContent().build();
    }
}
