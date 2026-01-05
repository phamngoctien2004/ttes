package com.demo.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.demo.news.repository.mysql")
@EnableElasticsearchRepositories(basePackages = "com.demo.news.repository.elasticsearch")
public class NewsSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsSearchApplication.class, args);
    }
}
