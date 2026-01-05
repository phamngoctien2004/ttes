package com.demo.news.service;

import com.demo.news.document.NewsDocument;
import com.demo.news.dto.NewsDTO;
import com.demo.news.dto.SearchRequest;
import com.demo.news.entity.NewsEntity;
import com.demo.news.repository.elasticsearch.NewsSearchRepository;
import com.demo.news.repository.mysql.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsSearchRepository newsSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public NewsDTO createNews(NewsDTO newsDTO) {
        // Lưu vào MySQL
        NewsEntity entity = convertToEntity(newsDTO);
        NewsEntity savedEntity = newsRepository.save(entity);
        
        // Đồng bộ vào Elasticsearch
        NewsDocument document = convertToDocument(savedEntity);
        newsSearchRepository.save(document);
        
        log.info("Created news with id: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    @Transactional
    public NewsDTO updateNews(Long id, NewsDTO newsDTO) {
        NewsEntity entity = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        
        entity.setTitle(newsDTO.getTitle());
        entity.setContent(newsDTO.getContent());
        entity.setAuthor(newsDTO.getAuthor());
        entity.setCategory(newsDTO.getCategory());
        entity.setTags(newsDTO.getTags());
        entity.setPublishedDate(newsDTO.getPublishedDate());
        
        NewsEntity updatedEntity = newsRepository.save(entity);
        
        // Cập nhật Elasticsearch
        NewsDocument document = convertToDocument(updatedEntity);
        newsSearchRepository.save(document);
        
        log.info("Updated news with id: {}", id);
        return convertToDTO(updatedEntity);
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
        newsSearchRepository.deleteById(String.valueOf(id));
        log.info("Deleted news with id: {}", id);
    }

    public NewsDTO getNewsById(Long id) {
        NewsEntity entity = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        return convertToDTO(entity);
    }

    public List<NewsDTO> getAllNews() {
        return newsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NewsDTO> searchNews(SearchRequest request) {
        // Sử dụng NativeQuery cho fuzzy search và tìm kiếm nâng cao
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        boolean hasKeyword = false;
        
        // Tìm kiếm theo keyword với fuzzy search và multi-match
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            hasKeyword = true;
            // Multi-match query với fuzzy và boosting
            // Search chính: title (^3), tags (^2) - boost cao
            // Search phụ: content (^0.5) - boost thấp
            boolQueryBuilder.must(q -> q
                .multiMatch(m -> m
                    .query(request.getKeyword())
                    .fields("title^3", "tags^2", "content^0.5")
                    .fuzziness("AUTO")  // Cho phép fuzzy search
                    .prefixLength(2)    // Tối thiểu 2 ký tự đầu phải match
                    .type(TextQueryType.BestFields)
                )
            );
        }
        
        // Lọc theo category
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            boolQueryBuilder.filter(f -> f
                .term(t -> t
                    .field("category")
                    .value(request.getCategory())
                )
            );
        }
        
        // Lọc theo author
        if (request.getAuthor() != null && !request.getAuthor().isEmpty()) {
            boolQueryBuilder.filter(f -> f
                .term(t -> t
                    .field("author")
                    .value(request.getAuthor())
                )
            );
        }
        
        // Lọc theo khoảng thời gian
        if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
            LocalDateTime startDate = LocalDateTime.parse(request.getStartDate(), 
                    DateTimeFormatter.ISO_DATE_TIME);
            boolQueryBuilder.filter(f -> f
                .range(r -> r
                    .date(d -> d
                        .field("publishedDate")
                        .gte(startDate.toString())
                    )
                )
            );
        }
        
        if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
            LocalDateTime endDate = LocalDateTime.parse(request.getEndDate(), 
                    DateTimeFormatter.ISO_DATE_TIME);
            boolQueryBuilder.filter(f -> f
                .range(r -> r
                    .date(d -> d
                        .field("publishedDate")
                        .lte(endDate.toString())
                    )
                )
            );
        }
        
        // Nếu không có keyword, sử dụng match_all
        if (!hasKeyword) {
            boolQueryBuilder.must(q -> q.matchAll(m -> m));
        }
        
        Query query = NativeQuery.builder()
            .withQuery(q -> q.bool(boolQueryBuilder.build()))
            .withPageable(PageRequest.of(request.getPage(), request.getSize()))
            .build();
        
        SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(query, NewsDocument.class);
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(this::convertDocumentToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void syncAllToElasticsearch() {
        List<NewsEntity> allNews = newsRepository.findAll();
        List<NewsDocument> documents = allNews.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        
        newsSearchRepository.saveAll(documents);
        log.info("Synced {} news to Elasticsearch", documents.size());
    }

    private NewsEntity convertToEntity(NewsDTO dto) {
        NewsEntity entity = new NewsEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setAuthor(dto.getAuthor());
        entity.setCategory(dto.getCategory());
        entity.setTags(dto.getTags());
        entity.setPublishedDate(dto.getPublishedDate());
        return entity;
    }

    private NewsDocument convertToDocument(NewsEntity entity) {
        NewsDocument document = new NewsDocument();
        document.setId(String.valueOf(entity.getId()));
        document.setTitle(entity.getTitle());
        document.setContent(entity.getContent());
        document.setAuthor(entity.getAuthor());
        document.setCategory(entity.getCategory());
        document.setTags(entity.getTags());
        document.setPublishedDate(entity.getPublishedDate());
        document.setCreatedAt(entity.getCreatedAt());
        document.setUpdatedAt(entity.getUpdatedAt());
        return document;
    }

    private NewsDTO convertToDTO(NewsEntity entity) {
        NewsDTO dto = new NewsDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setAuthor(entity.getAuthor());
        dto.setCategory(entity.getCategory());
        dto.setTags(entity.getTags());
        dto.setPublishedDate(entity.getPublishedDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private NewsDTO convertDocumentToDTO(NewsDocument document) {
        NewsDTO dto = new NewsDTO();
        dto.setId(Long.valueOf(document.getId()));
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setAuthor(document.getAuthor());
        dto.setCategory(document.getCategory());
        dto.setTags(document.getTags());
        dto.setPublishedDate(document.getPublishedDate());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }
}
