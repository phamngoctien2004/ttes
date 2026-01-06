package com.demo.news.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.presign-expiry}")
    private int presignExpiry;

    /**
     * Tạo presigned URL để upload file
     * @param fileName Tên file gốc
     * @param contentType MIME type của file
     * @return Presigned URL để upload
     */
    public String generateUploadPresignedUrl(String fileName, String contentType) {
        try {
            // String objectKey = generateObjectKey(fileName);
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(presignExpiry, TimeUnit.SECONDS)
                            .build()
            );
            log.info("Generated upload presigned URL for object: {}", fileName);
            return url;
        } catch (Exception e) {
            log.error("Error generating upload presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    /**
     * Tạo presigned URL để xem/download file
     * @param objectKey Key của object trong MinIO
     * @return Presigned URL để xem file
     */
    public String generateViewPresignedUrl(String objectKey) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(presignExpiry, TimeUnit.SECONDS)
                            .build()
            );
            log.info("Generated view presigned URL for object: {}", objectKey);
            return url;
        } catch (Exception e) {
            log.error("Error generating view presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate view URL", e);
        }
    }

    /**
     * Tạo object key duy nhất từ filename
     * Format: avatars/{userId}/{uuid}_{originalFileName}
     */
    public String generateObjectKey(String fileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(fileName);
        return uuid + extension;
    }

    /**
     * Tạo object key cho avatar của user cụ thể
     */
    public String generateAvatarKey(Long userId, String fileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(fileName);
        return "users/" + userId + "/avatar_" + uuid + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Xóa object từ MinIO
     * @param objectKey Key của object cần xóa
     */
    public void deleteObject(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            log.info("Deleted object from MinIO: {}", objectKey);
        } catch (Exception e) {
            log.error("Error deleting object from MinIO: {}", e.getMessage());
            throw new RuntimeException("Failed to delete object", e);
        }
    }

    public int getPresignExpiry() {
        return presignExpiry;
    }
}
