package com.demo.news.config;

import com.demo.news.entity.NewsEntity;
import com.demo.news.repository.mysql.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final NewsRepository newsRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ load dữ liệu nếu database còn trống
        if (newsRepository.count() == 0) {
            log.info("Loading sample news data...");
            loadSampleData();
            log.info("Sample data loaded successfully. Total news: {}", newsRepository.count());
        } else {
            log.info("Database already contains {} news articles", newsRepository.count());
        }
    }

    private void loadSampleData() {
        List<NewsEntity> newsList = Arrays.asList(
            createNews(
                "Trí tuệ nhân tạo đang thay đổi thế giới công nghệ",
                "AI và machine learning đang tạo ra những bước đột phá trong nhiều lĩnh vực. Từ nhận dạng hình ảnh, xử lý ngôn ngữ tự nhiên đến xe tự lái, công nghệ AI đang ngày càng phát triển mạnh mẽ.",
                "Nguyễn Văn A",
                "Technology",
                "AI, Machine Learning, Technology, Innovation",
                LocalDateTime.of(2026, 1, 1, 10, 0)
            ),
            createNews(
                "ChatGPT và tương lai của chatbot",
                "ChatGPT đã chứng minh khả năng của mô hình ngôn ngữ lớn trong việc tương tác với người dùng. Công nghệ này đang được ứng dụng rộng rãi trong nhiều lĩnh vực từ giáo dục đến dịch vụ khách hàng.",
                "Trần Thị B",
                "Technology",
                "ChatGPT, AI, NLP, Chatbot",
                LocalDateTime.of(2026, 1, 2, 9, 30)
            ),
            createNews(
                "Đội tuyển Việt Nam vô địch AFF Cup",
                "Trong trận chung kết kịch tính, đội tuyển Việt Nam đã giành chiến thắng 2-1 trước đối thủ trong hiệp phụ. Đây là lần thứ 3 Việt Nam vô địch giải đấu danh giá này.",
                "Lê Văn C",
                "Sports",
                "Football, Vietnam, AFF Cup, Sports",
                LocalDateTime.of(2026, 1, 3, 20, 0)
            ),
            createNews(
                "Kết quả bóng đá Champions League đêm qua",
                "Các trận đấu đã diễn ra vô cùng hấp dẫn với nhiều bàn thắng đẹp mắt. Real Madrid thắng 3-1, Manchester City hòa 2-2, và Bayern Munich thắng 4-0.",
                "Phạm Văn D",
                "Sports",
                "Football, Champions League, Soccer",
                LocalDateTime.of(2026, 1, 4, 7, 0)
            ),
            createNews(
                "Kinh tế Việt Nam tăng trưởng vượt kỳ vọng",
                "GDP quý đầu năm 2026 tăng 7.2%, vượt dự báo của các chuyên gia. Xuất khẩu và đầu tư FDI đều có những tín hiệu tích cực.",
                "Hoàng Thị E",
                "Business",
                "Economy, Vietnam, GDP, Business",
                LocalDateTime.of(2026, 1, 2, 14, 0)
            ),
            createNews(
                "Thị trường chứng khoán Việt Nam khởi sắc",
                "VN-Index tăng mạnh trong phiên giao dịch hôm nay, đạt mức cao nhất trong 6 tháng qua. Dòng tiền ngoại quay trở lại thị trường.",
                "Đặng Văn F",
                "Business",
                "Stock Market, Finance, Investment",
                LocalDateTime.of(2026, 1, 5, 11, 0)
            ),
            createNews(
                "Công nghệ Blockchain trong ngành tài chính",
                "Blockchain đang được các ngân hàng và tổ chức tài chính quan tâm. Công nghệ này hứa hẹn mang lại sự minh bạch và bảo mật cao trong các giao dịch.",
                "Vũ Thị G",
                "Technology",
                "Blockchain, FinTech, Cryptocurrency, Technology",
                LocalDateTime.of(2026, 1, 3, 16, 30)
            ),
            createNews(
                "5G - Thế hệ mạng di động mới",
                "Công nghệ 5G đang được triển khai rộng rãi tại Việt Nam. Tốc độ truyền tải nhanh gấp nhiều lần 4G, mở ra nhiều cơ hội cho IoT và smart city.",
                "Bùi Văn H",
                "Technology",
                "5G, Mobile, Network, Technology",
                LocalDateTime.of(2026, 1, 4, 13, 0)
            ),
            createNews(
                "Du lịch Việt Nam hút khách quốc tế",
                "Lượng khách du lịch quốc tế đến Việt Nam trong tháng đầu năm tăng 25% so với cùng kỳ. Hạ Long, Phú Quốc, Đà Nẵng là những điểm đến được yêu thích.",
                "Ngô Thị I",
                "Travel",
                "Tourism, Vietnam, Travel, Vacation",
                LocalDateTime.of(2026, 1, 5, 8, 0)
            ),
            createNews(
                "Khám phá ẩm thực đường phố Sài Gòn",
                "Bánh mì, phở, bún chả, cơm tấm... Ẩm thực đường phố Sài Gòn luôn là điểm đến hấp dẫn với du khách trong và ngoài nước.",
                "Trương Văn K",
                "Food",
                "Food, Vietnamese Cuisine, Street Food, Saigon",
                LocalDateTime.of(2026, 1, 4, 18, 0)
            ),
            createNews(
                "Python 3.13 ra mắt với nhiều tính năng mới",
                "Phiên bản Python 3.13 chính thức được phát hành với cải tiến về hiệu suất và nhiều tính năng mới. JIT compiler được tích hợp làm tăng tốc độ thực thi.",
                "Nguyễn Văn A",
                "Technology",
                "Python, Programming, Software Development",
                LocalDateTime.of(2026, 1, 5, 9, 0)
            ),
            createNews(
                "Bóng rổ NBA: Lakers vs Warriors",
                "Trận đấu giữa Lakers và Warriors đêm qua đã diễn ra vô cùng kịch tính. LeBron James ghi 35 điểm giúp Lakers giành chiến thắng 108-105.",
                "Lê Văn C",
                "Sports",
                "Basketball, NBA, Sports",
                LocalDateTime.of(2026, 1, 5, 10, 0)
            )
        );

        newsRepository.saveAll(newsList);
    }

    private NewsEntity createNews(String title, String content, String author, 
                                   String category, String tags, LocalDateTime publishedDate) {
        NewsEntity news = new NewsEntity();
        news.setTitle(title);
        news.setContent(content);
        news.setAuthor(author);
        news.setCategory(category);
        news.setTags(tags);
        news.setPublishedDate(publishedDate);
        return news;
    }
}
