package com.demo.news.controller;

import com.demo.news.dto.auth.UserInfoResponse;
import com.demo.news.dto.file.PresignedUrlResponse;
import com.demo.news.entity.UserEntity;
import com.demo.news.service.AuthService;
import com.demo.news.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final MinioService minioService;

    /**
     * Lấy thông tin user hiện tại (phải có token)
     * Avatar URL sẽ là presigned URL để tránh truy cập trái phép
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        UserInfoResponse response = authService.getCurrentUserInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo presigned URL để upload avatar
     * FE sẽ dùng URL này để upload file trực tiếp lên MinIO
     */
    @PostMapping("/me/avatar/upload-url")
    public ResponseEntity<PresignedUrlResponse> getAvatarUploadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "image/jpeg") String contentType) {
        
        UserEntity currentUser = authService.getCurrentUser();
        
        // Tạo key duy nhất cho avatar của user
        String objectKey = minioService.generateAvatarKey(currentUser.getId(), fileName);
        
        // Tạo presigned URL để upload
        String uploadUrl = minioService.generateUploadPresignedUrl(objectKey, contentType);
        
        // Lưu key vào database
        authService.updateAvatarKey(objectKey);

        return ResponseEntity.ok(PresignedUrlResponse.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .expiresIn(minioService.getPresignExpiry())
                .method("PUT")
                .build());
    }
}
