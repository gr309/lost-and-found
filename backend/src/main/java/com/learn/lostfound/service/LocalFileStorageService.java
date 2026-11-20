package com.learn.lostfound.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {
    private static final String URL_PREFIX = "/uploads/items/";
    private final Path itemsPath;
    private final ImageProcessor imageProcessor;

    public LocalFileStorageService(@Value("${app.upload-dir}") String uploadDir,
                                   ImageProcessor imageProcessor) throws IOException {
        this.itemsPath = Path.of(uploadDir).toAbsolutePath().normalize().resolve("items");
        this.imageProcessor = imageProcessor;
        Files.createDirectories(itemsPath);
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        byte[] image = imageProcessor.process(file);
        String filename = UUID.randomUUID() + ".jpg";
        Path temporary = Files.createTempFile(itemsPath, ".upload-", ".tmp");
        try {
            Files.write(temporary, image);
            Files.move(temporary, itemsPath.resolve(filename), StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporary);
        }
        return URL_PREFIX + filename;
    }

    @Override
    public void delete(String imageUrl) throws IOException {
        // Never interpret legacy external URLs or arbitrary paths as local files.
        if (imageUrl != null && imageUrl.matches(
                "/uploads/items/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.jpg")) {
            Files.deleteIfExists(itemsPath.resolve(imageUrl.substring(URL_PREFIX.length())));
        }
    }
}
