
package com.example.website.service.impl;

import com.example.website.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${file.storage}")
    private String storageType;

    @Value("${file.local.path}")
    private String localPath;

    @Value("${file.minio.endpoint}")
    private String minioEndpoint;

    @Value("${file.minio.access-key}")
    private String minioAccessKey;

    @Value("${file.minio.secret-key}")
    private String minioSecretKey;

    @Value("${file.minio.bucket-name}")
    private String minioBucketName;

    @Override
    public String upload(MultipartFile file) {
        String fileName = generateFileName(file.getOriginalFilename());
        
        if ("minio".equalsIgnoreCase(storageType)) {
            return uploadToMinio(file, fileName);
        } else {
            return uploadToLocal(file, fileName);
        }
    }

    @Override
    public void delete(String filePath) {
        if ("minio".equalsIgnoreCase(storageType)) {
            deleteFromMinio(filePath);
        } else {
            deleteFromLocal(filePath);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        if ("minio".equalsIgnoreCase(storageType)) {
            return minioEndpoint + "/" + minioBucketName + "/" + fileName;
        } else {
            return "/api/public/file/" + fileName;
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return date + "/" + UUID.randomUUID().toString() + extension;
    }

    private String uploadToLocal(MultipartFile file, String fileName) {
        try {
            Path uploadPath = Paths.get(localPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private String uploadToMinio(MultipartFile file, String fileName) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioEndpoint)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
            
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioBucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private void deleteFromLocal(String filePath) {
        try {
            Path path = Paths.get(localPath).resolve(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    private void deleteFromMinio(String filePath) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioEndpoint)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
            minioClient.removeObject(io.minio.RemoveObjectArgs.builder()
                    .bucket(minioBucketName)
                    .object(filePath)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }
}
