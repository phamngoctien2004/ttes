# News Search Demo với Elasticsearch

Demo ứng dụng tìm kiếm tin tức sử dụng Elasticsearch, Spring Boot và MySQL.

## Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.2.1**
- **Elasticsearch 9.2.3**
- **MySQL 8.0**
- **Docker & Docker Compose**

## Cấu trúc project

```
demo1/
├── compose.yml                   # Docker Compose config
├── pom.xml                       # Maven dependencies
├── src/
│   └── main/
│       ├── java/com/demo/news/
│       │   ├── NewsSearchApplication.java    # Main application
│       │   ├── config/
│       │   │   └── ElasticsearchConfig.java  # Elasticsearch configuration
│       │   ├── controller/
│       │   │   └── NewsController.java       # REST endpoints
│       │   ├── dto/
│       │   │   ├── NewsDTO.java              # Data transfer object
│       │   │   └── SearchRequest.java        # Search request model
│       │   ├── document/
│       │   │   └── NewsDocument.java         # Elasticsearch document
│       │   ├── entity/
│       │   │   └── NewsEntity.java           # MySQL entity
│       │   ├── repository/
│       │   │   ├── elasticsearch/
│       │   │   │   └── NewsSearchRepository.java
│       │   │   └── mysql/
│       │   │       └── NewsRepository.java
│       │   └── service/
│       │       └── NewsService.java          # Business logic
│       └── resources/
│           ├── application.yml               # Application config
│           └── elasticsearch-settings.json   # ES index settings
```

## Hướng dẫn chạy

### 1. Khởi động Docker containers

```bash
docker-compose up -d
```

Services sẽ được khởi động:
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- MySQL: localhost:3306

### 2. Build và chạy ứng dụng Spring Boot

```bash
mvn clean install
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: http://localhost:8080

## API Endpoints

### 1. Tạo tin tức mới
```bash
POST http://localhost:8080/api/news
Content-Type: application/json

{
  "title": "Tin tức về công nghệ AI",
  "content": "Nội dung chi tiết về AI và machine learning...",
  "author": "Nguyễn Văn A",
  "category": "Technology",
  "tags": "AI, Machine Learning, Tech",
  "publishedDate": "2026-01-05T10:00:00"
}
```

### 2. Lấy tất cả tin tức
```bash
GET http://localhost:8080/api/news
```

### 3. Lấy tin tức theo ID
```bash
GET http://localhost:8080/api/news/{id}
```

### 4. Cập nhật tin tức
```bash
PUT http://localhost:8080/api/news/{id}
Content-Type: application/json

{
  "title": "Tiêu đề cập nhật",
  "content": "Nội dung cập nhật...",
  "author": "Tác giả",
  "category": "Category",
  "tags": "tags"
}
```

### 5. Xóa tin tức
```bash
DELETE http://localhost:8080/api/news/{id}
```

### 6. Tìm kiếm và lọc tin tức (Elasticsearch)
```bash
POST http://localhost:8080/api/news/search
Content-Type: application/json

{
  "keyword": "AI",
  "category": "Technology",
  "author": "Nguyễn Văn A",
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-12-31T23:59:59",
  "page": 0,
  "size": 10
}
```

Tham số tìm kiếm:
- `keyword`: Tìm kiếm trong title và content
- `category`: Lọc theo danh mục
- `author`: Lọc theo tác giả
- `startDate`: Lọc từ ngày (ISO format)
- `endDate`: Lọc đến ngày (ISO format)
- `page`: Số trang (mặc định: 0)
- `size`: Số lượng kết quả mỗi trang (mặc định: 10)

### 7. Đồng bộ dữ liệu từ MySQL sang Elasticsearch
```bash
POST http://localhost:8080/api/news/sync
```

## Tính năng

✅ CRUD operations cho tin tức  
✅ Tự động đồng bộ MySQL ↔️ Elasticsearch  
✅ Full-text search với Elasticsearch  
✅ Lọc theo category, author  
✅ Lọc theo khoảng thời gian  
✅ Phân trang kết quả tìm kiếm  

## Kiểm tra Elasticsearch

### Xem tất cả indices
```bash
curl http://localhost:9200/_cat/indices?v
```

### Tìm kiếm trong index news
```bash
curl -X GET "http://localhost:9200/news/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": {
      "title": "AI"
    }
  }
}
'
```

## Database Schema (MySQL)

Bảng `news`:
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `title` (VARCHAR(500), NOT NULL)
- `content` (TEXT)
- `author` (VARCHAR(100))
- `category` (VARCHAR(100))
- `tags` (VARCHAR(500))
- `published_date` (DATETIME)
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

## Lưu ý

1. Đảm bảo Docker đang chạy trước khi khởi động services
2. Chờ Elasticsearch khởi động hoàn toàn (~30s) trước khi chạy ứng dụng
3. Database `news_db` sẽ tự động được tạo khi khởi động MySQL
4. Index `news` trong Elasticsearch sẽ tự động được tạo khi chạy ứng dụng

## Dừng services

```bash
docker-compose down
```

Để xóa cả volumes (xóa dữ liệu):
```bash
docker-compose down -v
```
