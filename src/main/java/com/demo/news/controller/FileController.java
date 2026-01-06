package com.demo.news.controller;

import com.demo.news.dto.file.FileUrlResponse;
import com.demo.news.dto.file.PresignedUrlResponse;
import com.demo.news.entity.UserEntity;
import com.demo.news.service.AuthService;
import com.demo.news.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;
    private final AuthService authService;

    /**
     * Tạo presigned URL để upload file
     * FE sẽ dùng URL này để upload file trực tiếp lên MinIO
     * 
     * @param fileName Tên file gốc
     * @param contentType MIME type của file (ví dụ: image/jpeg, application/pdf)
     * @return Presigned URL để upload
     */
    @PostMapping("/upload-url")
    public ResponseEntity<PresignedUrlResponse> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "application/octet-stream") String contentType) {
        
        UserEntity currentUser = authService.getCurrentUser();
        
        // Tạo key duy nhất cho file
        String objectKey = "files/" + currentUser.getId() + "/" + minioService.generateObjectKey(fileName);
        
        // Tạo presigned URL để upload
        String uploadUrl = minioService.generateUploadPresignedUrl(objectKey, contentType);

        return ResponseEntity.ok(PresignedUrlResponse.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .expiresIn(minioService.getPresignExpiry())
                .method("PUT")
                .build());
    }

    /**
     * Tạo presigned URL để xem/download file
     * Chỉ cho phép user xem file của chính mình
     * 
     * @param objectKey Key của object trong MinIO
     * @return Presigned URL để xem file
     */
    @GetMapping("/view-url")
    public ResponseEntity<FileUrlResponse> getViewUrl(@RequestParam String objectKey) {
        UserEntity currentUser = authService.getCurrentUser();
        
        // Kiểm tra xem objectKey có thuộc về user hiện tại không
        String userPrefix = "files/" + currentUser.getId() + "/";
        String avatarPrefix = "users/" + currentUser.getId() + "/";
        
        if (!objectKey.startsWith(userPrefix) && !objectKey.startsWith(avatarPrefix)) {
            throw new RuntimeException("Access denied: You can only view your own files");
        }
        
        // Tạo presigned URL để xem
        String viewUrl = minioService.generateViewPresignedUrl(objectKey);

        return ResponseEntity.ok(FileUrlResponse.builder()
                .viewUrl(viewUrl)
                .objectKey(objectKey)
                .expiresIn(minioService.getPresignExpiry())
                .build());
    }

    /**
     * Xóa avatar của user hiện tại
     * Xóa file từ MinIO và cập nhật avatarKey = null trong database
     * 
     * @return ResponseEntity với message thành công
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<?> deleteAvatar() {
        UserEntity currentUser = authService.getCurrentUser();
        
        String avatarKey = currentUser.getAvatarKey();
        if (avatarKey == null || avatarKey.isEmpty()) {
            throw new RuntimeException("No avatar to delete");
        }
        
        // Xóa file từ MinIO
        minioService.deleteObject(avatarKey);
        
        // Cập nhật avatarKey = null trong database
        authService.updateAvatarKey(null);
        
        return ResponseEntity.ok().body(java.util.Map.of("message", "Avatar deleted successfully"));
    }
}
