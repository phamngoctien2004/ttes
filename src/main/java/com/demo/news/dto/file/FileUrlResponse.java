package com.demo.news.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUrlResponse {
    private String viewUrl;        // Presigned URL để xem file
    private String objectKey;      // Key của object
    private long expiresIn;        // Thời gian hết hạn (seconds)
}
