package com.demo.news.config;

import com.demo.news.entity.NewsEntity;
import com.demo.news.repository.mysql.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class LargeDataLoader implements CommandLineRunner {

    private final NewsRepository newsRepository;
    private final Random random = new Random();

    // Categories
    private final String[] CATEGORIES = {
        "Technology", "Sports", "Business", "Travel", "Food", 
        "Health", "Entertainment", "Science", "Education", "Politics"
    };

    // Authors
    private final String[] AUTHORS = {
        "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D", "Hoàng Văn E",
        "Đặng Thị F", "Vũ Văn G", "Bùi Thị H", "Ngô Văn I", "Trương Thị K",
        "Mai Văn L", "Đỗ Thị M", "Hồ Văn N", "Dương Thị O", "Lý Văn P"
    };

    // Title templates by category
    private final String[][] TITLE_TEMPLATES = {
        // Technology
        {
            "Công nghệ {} đang thay đổi thế giới",
            "Xu hướng {} trong năm {}",
            "{} - Tương lai của công nghệ",
            "Đột phá mới trong lĩnh vực {}",
            "Top {} xu hướng {} hot nhất",
            "{} và ứng dụng trong thực tế",
            "Cập nhật mới nhất về {}",
            "{} - Cơ hội và thách thức"
        },
        // Sports
        {
            "Kết quả {} hôm nay",
            "Đội tuyển {} giành chiến thắng",
            "Tin tức {} mới nhất",
            "Lịch thi đấu {} tuần này",
            "Ngôi sao {} tỏa sáng",
            "{} - Trận cầu đỉnh cao",
            "Phân tích trận {} vs {}",
            "Top {} cầu thủ xuất sắc nhất"
        },
        // Business
        {
            "Thị trường {} tăng trưởng mạnh",
            "Kinh tế {} trong quý {}",
            "Đầu tư vào {} năm {}",
            "Doanh nghiệp {} phát triển",
            "Xu hướng {} trong kinh doanh",
            "{} - Cơ hội đầu tư hấp dẫn",
            "Phân tích thị trường {}",
            "Chiến lược {} cho doanh nghiệp"
        },
        // Travel
        {
            "Du lịch {} - Điểm đến hấp dẫn",
            "Khám phá vẻ đẹp của {}",
            "{} - Thiên đường du lịch",
            "Kinh nghiệm du lịch {} tiết kiệm",
            "Top {} địa điểm đẹp nhất tại {}",
            "Hành trình khám phá {}",
            "{} - Điểm đến không thể bỏ qua",
            "Lịch trình du lịch {} {} ngày"
        },
        // Food
        {
            "Món {} ngon không thể bỏ qua",
            "Bí quyết làm {} chuẩn vị",
            "Top {} món {} ngon nhất",
            "Khám phá ẩm thực {}",
            "{} - Đặc sản nổi tiếng",
            "Công thức làm {} đơn giản",
            "Nhà hàng {} nổi tiếng",
            "{} - Hương vị khó quên"
        }
    };

    // Tech keywords
    private final String[] TECH_KEYWORDS = {
        "AI", "Machine Learning", "Blockchain", "Cloud Computing", "5G",
        "IoT", "Big Data", "Cybersecurity", "AR/VR", "Quantum Computing"
    };

    // Sports keywords
    private final String[] SPORTS_KEYWORDS = {
        "Bóng đá", "Bóng rổ", "Tennis", "Cầu lông", "Bơi lội",
        "Võ thuật", "Marathon", "Yoga", "Fitness", "Golf"
    };

    @Override
    public void run(String... args) throws Exception {
        long count = newsRepository.count();
        if (count < 100) {
            log.info("Generating 1000 news articles...");
            List<NewsEntity> newsList = generateNews(1000);
            newsRepository.saveAll(newsList);
            log.info("Successfully generated {} news articles", newsList.size());
        } else {
            log.info("Database already has {} news, skipping large data generation", count);
        }
    }

    private List<NewsEntity> generateNews(int count) {
        List<NewsEntity> newsList = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        for (int i = 0; i < count; i++) {
            NewsEntity news = new NewsEntity();
            String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
            
            news.setCategory(category);
            news.setAuthor(AUTHORS[random.nextInt(AUTHORS.length)]);
            news.setTitle(generateTitle(category, i));
            news.setContent(generateContent(category, i));
            news.setTags(generateTags(category));
            
            // Random date trong năm 2025-2026
            int daysToAdd = random.nextInt(400);
            news.setPublishedDate(baseDate.plusDays(daysToAdd));
            
            newsList.add(news);
        }

        return newsList;
    }

    private String generateTitle(String category, int index) {
        int categoryIndex = getCategoryIndex(category);
        String[] templates = TITLE_TEMPLATES[Math.min(categoryIndex, TITLE_TEMPLATES.length - 1)];
        String template = templates[random.nextInt(templates.length)];
        
        switch (category) {
            case "Technology":
                String tech = TECH_KEYWORDS[random.nextInt(TECH_KEYWORDS.length)];
                return template.replace("{}", tech);
            case "Sports":
                String sport = SPORTS_KEYWORDS[random.nextInt(SPORTS_KEYWORDS.length)];
                return template.replace("{}", sport);
            case "Business":
                return template.replace("{}", "chứng khoán").replace("{}", "I");
            case "Travel":
                String[] places = {"Đà Nẵng", "Phú Quốc", "Hạ Long", "Sapa", "Nha Trang", "Đà Lạt"};
                return template.replace("{}", places[random.nextInt(places.length)]);
            case "Food":
                String[] foods = {"phở", "bún chả", "bánh mì", "cơm tấm", "gỏi cuốn"};
                return template.replace("{}", foods[random.nextInt(foods.length)]);
            default:
                return category + " - Tin tức số " + (index + 1);
        }
    }

    private String generateContent(String category, int index) {
        StringBuilder content = new StringBuilder();
        
        content.append("Tin tức về ").append(category.toLowerCase()).append(". ");
        
        String[] sentences = {
            "Đây là một sự phát triển quan trọng trong lĩnh vực này. ",
            "Các chuyên gia đánh giá cao tiềm năng phát triển. ",
            "Xu hướng này đang thu hút sự quan tâm của nhiều người. ",
            "Có nhiều cơ hội và thách thức đi kèm. ",
            "Công nghệ và đổi mới đóng vai trò quan trọng. ",
            "Người dùng đang rất quan tâm đến vấn đề này. ",
            "Thị trường đang có nhiều biến động tích cực. ",
            "Các doanh nghiệp đang đầu tư mạnh vào lĩnh vực này. ",
            "Tương lai của ngành này rất hứa hẹn. ",
            "Nhiều chuyên gia đưa ra nhận định tích cực. "
        };

        // Add 5-10 random sentences
        int sentenceCount = 5 + random.nextInt(6);
        for (int i = 0; i < sentenceCount; i++) {
            content.append(sentences[random.nextInt(sentences.length)]);
        }

        return content.toString();
    }

    private String generateTags(String category) {
        List<String> tags = new ArrayList<>();
        tags.add(category);

        switch (category) {
            case "Technology":
                tags.add(TECH_KEYWORDS[random.nextInt(TECH_KEYWORDS.length)]);
                tags.add(TECH_KEYWORDS[random.nextInt(TECH_KEYWORDS.length)]);
                tags.add("Innovation");
                break;
            case "Sports":
                tags.add(SPORTS_KEYWORDS[random.nextInt(SPORTS_KEYWORDS.length)]);
                tags.add("Competition");
                break;
            case "Business":
                tags.add("Economy");
                tags.add("Investment");
                tags.add("Finance");
                break;
            case "Travel":
                tags.add("Tourism");
                tags.add("Adventure");
                tags.add("Vietnam");
                break;
            case "Food":
                tags.add("Vietnamese Cuisine");
                tags.add("Recipe");
                tags.add("Cooking");
                break;
            default:
                tags.add("News");
                tags.add("Update");
        }

        return String.join(", ", tags);
    }

    private int getCategoryIndex(String category) {
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(category)) {
                return i % TITLE_TEMPLATES.length;
            }
        }
        return 0;
    }
}
