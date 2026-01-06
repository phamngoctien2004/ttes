package com.demo.news.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl; // Presigned URL để xem avatar
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
