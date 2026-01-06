package com.demo.news.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
    private String uploadUrl;      // Presigned URL để upload
    private String objectKey;      // Key của object trong MinIO
    private long expiresIn;        // Thời gian hết hạn (seconds)
    private String method;         // HTTP method (PUT cho upload)
}
