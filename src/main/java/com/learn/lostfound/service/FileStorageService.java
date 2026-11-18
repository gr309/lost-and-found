package com.learn.lostfound.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final Path uploadPath = Paths.get("uploads/items").toAbsolutePath().normalize();

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Maximum image size is 5MB");
        }   

        String contentType = file.getContentType();

        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
            throw new IllegalArgumentException("Invalid image type");
        }

        Files.createDirectories(uploadPath);

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Invalid image type");
        };

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), destination);

        return "/uploads/items/" + filename;
    }
}