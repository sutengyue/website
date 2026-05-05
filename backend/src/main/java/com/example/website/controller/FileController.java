
package com.example.website.controller;

import com.example.website.common.ResponseResult;
import com.example.website.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Value("${file.local.path}")
    private String localPath;

    @PostMapping("/admin/upload")
    public ResponseResult<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String fileName = fileService.upload(file);
        String url = fileService.getFileUrl(fileName);
        
        Map<String, String> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("url", url);
        
        return ResponseResult.success(result);
    }

    @GetMapping("/public/file/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Path filePath = Paths.get(localPath).resolve(fileName);
        Resource resource = new FileSystemResource(filePath);
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = getContentType(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}
