# API Documentation - News Search System

## 📌 Tổng quan

API RESTful cho hệ thống quản lý và tìm kiếm tin tức với Elasticsearch.

**Base URL:** `http://localhost:8080`

**Content-Type:** `application/json`

**CORS:** Cho phép tất cả origins (`*`)

---

## 🔑 Data Models

### NewsDTO
```typescript
interface NewsDTO {
  id?: number;                    // Auto-generated, không cần khi tạo mới
  title: string;                  // Tiêu đề tin tức (required, max 500 chars)
  content: string;                // Nội dung tin tức (TEXT)
  author: string;                 // Tác giả (max 100 chars)
  category: string;               // Danh mục (max 100 chars)
  tags: string;                   // Tags, phân cách bằng dấu phẩy (max 500 chars)
  publishedDate: string;          // ISO 8601 format: "2026-01-05T10:00:00"
  createdAt?: string;             // Auto-generated
  updatedAt?: string;             // Auto-generated
}
```

### SearchRequest
```typescript
interface SearchRequest {
  keyword?: string;               // Từ khóa tìm kiếm (hỗ trợ fuzzy search)
  category?: string;              // Lọc theo danh mục (exact match)
  author?: string;                // Lọc theo tác giả (exact match)
  startDate?: string;             // Lọc từ ngày (ISO 8601: "2026-01-01T00:00:00")
  endDate?: string;               // Lọc đến ngày (ISO 8601: "2026-01-31T23:59:59")
  page?: number;                  // Số trang (bắt đầu từ 0), mặc định: 0
  size?: number;                  // Số kết quả mỗi trang, mặc định: 10
}
```

---

## 📋 API Endpoints

### 1. Lấy tất cả tin tức

**Endpoint:** `GET /api/news`

**Mô tả:** Lấy danh sách tất cả tin tức từ database (không qua Elasticsearch)

**Request:**
```http
GET /api/news HTTP/1.1
Host: localhost:8080
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Trí tuệ nhân tạo đang thay đổi thế giới công nghệ",
    "content": "AI và machine learning đang tạo ra những bước đột phá...",
    "author": "Nguyễn Văn A",
    "category": "Technology",
    "tags": "AI, Machine Learning, Technology, Innovation",
    "publishedDate": "2026-01-01T10:00:00",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  },
  {
    "id": 2,
    "title": "ChatGPT và tương lai của chatbot",
    "content": "ChatGPT đã chứng minh khả năng...",
    "author": "Trần Thị B",
    "category": "Technology",
    "tags": "ChatGPT, AI, NLP, Chatbot",
    "publishedDate": "2026-01-02T09:30:00",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

**Use Case FE:**
```typescript
async function getAllNews(): Promise<NewsDTO[]> {
  const response = await fetch('http://localhost:8080/api/news');
  if (!response.ok) throw new Error('Failed to fetch news');
  return response.json();
}
```

---

### 2. Lấy tin tức theo ID

**Endpoint:** `GET /api/news/{id}`

**Mô tả:** Lấy chi tiết một tin tức theo ID

**Parameters:**
- `id` (path, required): ID của tin tức

**Request:**
```http
GET /api/news/1 HTTP/1.1
Host: localhost:8080
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Trí tuệ nhân tạo đang thay đổi thế giới công nghệ",
  "content": "AI và machine learning đang tạo ra những bước đột phá...",
  "author": "Nguyễn Văn A",
  "category": "Technology",
  "tags": "AI, Machine Learning, Technology, Innovation",
  "publishedDate": "2026-01-01T10:00:00",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2026-01-05T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "News not found with id: 999",
  "path": "/api/news/999"
}
```

**Use Case FE:**
```typescript
async function getNewsById(id: number): Promise<NewsDTO> {
  const response = await fetch(`http://localhost:8080/api/news/${id}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Tin tức không tồn tại');
    }
    throw new Error('Failed to fetch news');
  }
  return response.json();
}
```

---

### 3. Tạo tin tức mới

**Endpoint:** `POST /api/news`

**Mô tả:** Tạo tin tức mới, tự động lưu vào MySQL và đồng bộ sang Elasticsearch

**Request Body:**
```json
{
  "title": "Tin tức mới về AI",
  "content": "Nội dung chi tiết về AI và machine learning...",
  "author": "Nguyễn Văn C",
  "category": "Technology",
  "tags": "AI, Tech, Innovation",
  "publishedDate": "2026-01-05T15:00:00"
}
```

**Response:** `201 Created`
```json
{
  "id": 13,
  "title": "Tin tức mới về AI",
  "content": "Nội dung chi tiết về AI và machine learning...",
  "author": "Nguyễn Văn C",
  "category": "Technology",
  "tags": "AI, Tech, Innovation",
  "publishedDate": "2026-01-05T15:00:00",
  "createdAt": "2026-01-05T15:00:00",
  "updatedAt": "2026-01-05T15:00:00"
}
```

**Validation Errors:** `400 Bad Request`
```json
{
  "timestamp": "2026-01-05T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/news"
}
```

**Use Case FE:**
```typescript
interface CreateNewsRequest {
  title: string;
  content: string;
  author: string;
  category: string;
  tags: string;
  publishedDate: string; // ISO 8601
}

async function createNews(data: CreateNewsRequest): Promise<NewsDTO> {
  const response = await fetch('http://localhost:8080/api/news', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to create news');
  }
  
  return response.json();
}

// Example usage
const newNews = await createNews({
  title: "Tin tức mới",
  content: "Nội dung...",
  author: "Admin",
  category: "Technology",
  tags: "AI, Tech",
  publishedDate: new Date().toISOString(),
});
```

---

### 4. Cập nhật tin tức

**Endpoint:** `PUT /api/news/{id}`

**Mô tả:** Cập nhật tin tức, tự động sync sang Elasticsearch

**Parameters:**
- `id` (path, required): ID của tin tức cần cập nhật

**Request Body:**
```json
{
  "title": "Tiêu đề đã cập nhật",
  "content": "Nội dung đã được chỉnh sửa...",
  "author": "Nguyễn Văn A",
  "category": "Technology",
  "tags": "AI, Updated",
  "publishedDate": "2026-01-05T16:00:00"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Tiêu đề đã cập nhật",
  "content": "Nội dung đã được chỉnh sửa...",
  "author": "Nguyễn Văn A",
  "category": "Technology",
  "tags": "AI, Updated",
  "publishedDate": "2026-01-05T16:00:00",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T16:00:00"
}
```

**Use Case FE:**
```typescript
async function updateNews(id: number, data: CreateNewsRequest): Promise<NewsDTO> {
  const response = await fetch(`http://localhost:8080/api/news/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Tin tức không tồn tại');
    }
    throw new Error('Failed to update news');
  }
  
  return response.json();
}
```

---

### 5. Xóa tin tức

**Endpoint:** `DELETE /api/news/{id}`

**Mô tả:** Xóa tin tức khỏi MySQL và Elasticsearch

**Parameters:**
- `id` (path, required): ID của tin tức cần xóa

**Request:**
```http
DELETE /api/news/1 HTTP/1.1
Host: localhost:8080
```

**Response:** `204 No Content`
```
(No response body)
```

**Use Case FE:**
```typescript
async function deleteNews(id: number): Promise<void> {
  const response = await fetch(`http://localhost:8080/api/news/${id}`, {
    method: 'DELETE',
  });
  
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Tin tức không tồn tại');
    }
    throw new Error('Failed to delete news');
  }
}

// Example with confirmation
async function handleDelete(id: number) {
  if (confirm('Bạn có chắc muốn xóa tin tức này?')) {
    await deleteNews(id);
    alert('Đã xóa thành công');
    // Reload list
  }
}
```

---

### 6. Tìm kiếm và lọc tin tức (Elasticsearch)

**Endpoint:** `POST /api/news/search`

**Mô tả:** Tìm kiếm tin tức với Elasticsearch. Hỗ trợ:
- ✅ Fuzzy search (sai chính tả 1-2 ký tự)
- ✅ Multi-field search (title có trọng số gấp đôi content)
- ✅ Lọc theo category, author, date range
- ✅ Phân trang

**Request Body:**
```json
{
  "keyword": "AI",
  "category": "Technology",
  "author": "Nguyễn Văn A",
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-01-31T23:59:59",
  "page": 0,
  "size": 10
}
```

**Tất cả các field đều optional**. Có thể gửi:
- Chỉ keyword
- Chỉ filters
- Kết hợp keyword + filters
- Rỗng (lấy tất cả)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Trí tuệ nhân tạo đang thay đổi thế giới công nghệ",
    "content": "AI và machine learning đang tạo ra những bước đột phá...",
    "author": "Nguyễn Văn A",
    "category": "Technology",
    "tags": "AI, Machine Learning, Technology, Innovation",
    "publishedDate": "2026-01-01T10:00:00",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

**Use Case FE:**

#### Case 1: Tìm kiếm đơn giản
```typescript
async function searchNews(searchParams: SearchRequest): Promise<NewsDTO[]> {
  const response = await fetch('http://localhost:8080/api/news/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(searchParams),
  });
  
  if (!response.ok) {
    throw new Error('Search failed');
  }
  
  return response.json();
}

// Tìm theo keyword
const results = await searchNews({ keyword: "AI", page: 0, size: 10 });
```

#### Case 2: Search bar với debounce
```typescript
import { useState, useEffect } from 'react';
import debounce from 'lodash/debounce';

function SearchBar() {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState<NewsDTO[]>([]);
  const [loading, setLoading] = useState(false);

  const debouncedSearch = debounce(async (searchTerm: string) => {
    if (!searchTerm.trim()) {
      setResults([]);
      return;
    }
    
    setLoading(true);
    try {
      const data = await searchNews({ keyword: searchTerm, page: 0, size: 5 });
      setResults(data);
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  }, 300);

  useEffect(() => {
    debouncedSearch(keyword);
  }, [keyword]);

  return (
    <div>
      <input 
        type="text" 
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        placeholder="Tìm kiếm tin tức..."
      />
      {loading && <p>Đang tìm kiếm...</p>}
      <ul>
        {results.map(news => (
          <li key={news.id}>{news.title}</li>
        ))}
      </ul>
    </div>
  );
}
```

#### Case 3: Advanced search với nhiều filters
```typescript
interface SearchFilters {
  keyword: string;
  category: string;
  author: string;
  dateFrom: Date | null;
  dateTo: Date | null;
}

function AdvancedSearch() {
  const [filters, setFilters] = useState<SearchFilters>({
    keyword: '',
    category: '',
    author: '',
    dateFrom: null,
    dateTo: null,
  });
  const [results, setResults] = useState<NewsDTO[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const handleSearch = async () => {
    const searchParams: SearchRequest = {
      keyword: filters.keyword || undefined,
      category: filters.category || undefined,
      author: filters.author || undefined,
      startDate: filters.dateFrom?.toISOString() || undefined,
      endDate: filters.dateTo?.toISOString() || undefined,
      page,
      size: 10,
    };

    const data = await searchNews(searchParams);
    setResults(data);
    // Giả sử backend trả về total count trong header
    // setTotalPages(Math.ceil(totalCount / 10));
  };

  return (
    <div>
      <input 
        placeholder="Từ khóa" 
        value={filters.keyword}
        onChange={e => setFilters({...filters, keyword: e.target.value})}
      />
      <select 
        value={filters.category}
        onChange={e => setFilters({...filters, category: e.target.value})}
      >
        <option value="">Tất cả danh mục</option>
        <option value="Technology">Technology</option>
        <option value="Sports">Sports</option>
        <option value="Business">Business</option>
      </select>
      <input 
        placeholder="Tác giả"
        value={filters.author}
        onChange={e => setFilters({...filters, author: e.target.value})}
      />
      {/* Date pickers... */}
      <button onClick={handleSearch}>Tìm kiếm</button>
      
      {/* Results */}
      <div>
        {results.map(news => (
          <NewsCard key={news.id} news={news} />
        ))}
      </div>
      
      {/* Pagination */}
      <Pagination 
        page={page} 
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
}
```

#### Case 4: Lọc theo category (không cần keyword)
```typescript
// Lấy tất cả tin Technology
const techNews = await searchNews({ 
  category: "Technology",
  page: 0,
  size: 20 
});
```

#### Case 5: Lọc theo khoảng thời gian
```typescript
// Lấy tin trong tháng 1/2026
const januaryNews = await searchNews({
  startDate: "2026-01-01T00:00:00",
  endDate: "2026-01-31T23:59:59",
  page: 0,
  size: 50
});
```

#### Case 6: Fuzzy search test
```typescript
// Tìm "technlogy" (sai chính tả) vẫn tìm được "technology"
const results = await searchNews({ 
  keyword: "technlogy",
  page: 0,
  size: 10 
});
// Kết quả vẫn trả về các tin có "technology"
```

---

### 7. Đồng bộ dữ liệu MySQL → Elasticsearch

**Endpoint:** `POST /api/news/sync`

**Mô tả:** Đồng bộ toàn bộ dữ liệu từ MySQL sang Elasticsearch. Thường dùng khi:
- Khởi động lần đầu
- Elasticsearch bị lỗi và cần rebuild index
- Có thay đổi mapping

**Request:**
```http
POST /api/news/sync HTTP/1.1
Host: localhost:8080
```

**Response:** `200 OK`
```json
"Synced all news to Elasticsearch"
```

**Use Case FE:**
```typescript
async function syncToElasticsearch(): Promise<string> {
  const response = await fetch('http://localhost:8080/api/news/sync', {
    method: 'POST',
  });
  
  if (!response.ok) {
    throw new Error('Sync failed');
  }
  
  return response.json();
}

// Example: Admin panel button
async function handleSync() {
  if (confirm('Đồng bộ toàn bộ dữ liệu sang Elasticsearch?')) {
    const result = await syncToElasticsearch();
    alert(result);
  }
}
```

---

## 🎯 Use Cases tổng hợp

### 1. Trang chủ hiển thị tin mới nhất
```typescript
async function loadHomePage() {
  // Lấy 10 tin mới nhất
  const allNews = await getAllNews();
  const latestNews = allNews
    .sort((a, b) => new Date(b.publishedDate).getTime() - new Date(a.publishedDate).getTime())
    .slice(0, 10);
  return latestNews;
}
```

### 2. Trang danh mục
```typescript
async function loadCategoryPage(category: string, page: number = 0) {
  return searchNews({
    category,
    page,
    size: 20,
  });
}

// Usage
const techNews = await loadCategoryPage('Technology', 0);
```

### 3. Trang chi tiết tin tức
```typescript
async function loadNewsDetail(id: number) {
  const news = await getNewsById(id);
  // Tìm tin liên quan cùng category
  const relatedNews = await searchNews({
    category: news.category,
    page: 0,
    size: 5,
  });
  // Loại bỏ tin hiện tại
  const filtered = relatedNews.filter(n => n.id !== id);
  return { news, relatedNews: filtered };
}
```

### 4. Auto-complete search
```typescript
async function getSearchSuggestions(query: string): Promise<string[]> {
  if (query.length < 2) return [];
  
  const results = await searchNews({ 
    keyword: query,
    page: 0,
    size: 5 
  });
  
  return results.map(n => n.title);
}
```

### 5. Dashboard admin
```typescript
async function loadAdminDashboard() {
  const allNews = await getAllNews();
  
  const stats = {
    total: allNews.length,
    byCategory: {} as Record<string, number>,
    byAuthor: {} as Record<string, number>,
  };
  
  allNews.forEach(news => {
    stats.byCategory[news.category] = (stats.byCategory[news.category] || 0) + 1;
    stats.byAuthor[news.author] = (stats.byAuthor[news.author] || 0) + 1;
  });
  
  return stats;
}
```

---

## ⚠️ Error Handling

### Error Response Format
```typescript
interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
```

### Common Errors

#### 400 Bad Request
```json
{
  "timestamp": "2026-01-05T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request body",
  "path": "/api/news"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2026-01-05T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "News not found with id: 999",
  "path": "/api/news/999"
}
```

#### 500 Internal Server Error
```json
{
  "timestamp": "2026-01-05T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Elasticsearch connection failed",
  "path": "/api/news/search"
}
```

### Error Handling trong FE
```typescript
async function apiCall<T>(
  url: string, 
  options?: RequestInit
): Promise<T> {
  try {
    const response = await fetch(url, options);
    
    if (!response.ok) {
      const error: ErrorResponse = await response.json();
      
      switch (error.status) {
        case 400:
          throw new Error(`Dữ liệu không hợp lệ: ${error.message}`);
        case 404:
          throw new Error('Không tìm thấy dữ liệu');
        case 500:
          throw new Error('Lỗi server, vui lòng thử lại sau');
        default:
          throw new Error(error.message || 'Có lỗi xảy ra');
      }
    }
    
    // Handle 204 No Content
    if (response.status === 204) {
      return null as T;
    }
    
    return response.json();
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('Không thể kết nối đến server');
    }
    throw error;
  }
}
```

---

## 📊 Pagination Best Practices

```typescript
interface PaginationState {
  page: number;
  size: number;
  hasMore: boolean;
}

function usePagination(initialSize: number = 10) {
  const [state, setState] = useState<PaginationState>({
    page: 0,
    size: initialSize,
    hasMore: true,
  });

  const loadMore = async (searchParams: Omit<SearchRequest, 'page' | 'size'>) => {
    const results = await searchNews({
      ...searchParams,
      page: state.page,
      size: state.size,
    });
    
    setState(prev => ({
      ...prev,
      page: prev.page + 1,
      hasMore: results.length === state.size,
    }));
    
    return results;
  };

  const reset = () => {
    setState({ page: 0, size: initialSize, hasMore: true });
  };

  return { state, loadMore, reset };
}
```

---

## 🔍 Search Tips cho FE

### 1. Fuzzy Search
- Hỗ trợ sai chính tả **1-2 ký tự**
- 2 ký tự đầu phải đúng
- Ví dụ: "technlogy" → tìm thấy "technology"

### 2. Boosting
- Title có trọng số **x2** so với content
- Tin có keyword trong title sẽ xuất hiện trước

### 3. Best Practices
- **Debounce** search input (300-500ms)
- **Minimum 2 characters** trước khi search
- **Cache** kết quả search phổ biến
- **Loading state** khi đang search
- **Empty state** khi không có kết quả

### 4. Performance
- Giới hạn `size` tối đa 50-100 items
- Sử dụng pagination thay vì load all
- Cache category filters

---

## 📝 Postman Collection

Import collection này vào Postman để test nhanh:

```json
{
  "info": {
    "name": "News Search API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All News",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/news"
      }
    },
    {
      "name": "Search News",
      "request": {
        "method": "POST",
        "url": "{{base_url}}/api/news/search",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"keyword\": \"AI\",\n  \"page\": 0,\n  \"size\": 10\n}"
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    }
  ]
}
```

---

## 🚀 Quick Start cho Frontend

### React Example
```typescript
// api/newsApi.ts
const BASE_URL = 'http://localhost:8080';

export const newsApi = {
  getAll: () => 
    fetch(`${BASE_URL}/api/news`).then(r => r.json()),
  
  getById: (id: number) => 
    fetch(`${BASE_URL}/api/news/${id}`).then(r => r.json()),
  
  create: (data: CreateNewsRequest) =>
    fetch(`${BASE_URL}/api/news`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(r => r.json()),
  
  search: (params: SearchRequest) =>
    fetch(`${BASE_URL}/api/news/search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    }).then(r => r.json()),
};
```

---

## 📧 Support

Có vấn đề? Contact: [your-email@example.com]

**API Version:** 1.0.0  
**Last Updated:** January 5, 2026
