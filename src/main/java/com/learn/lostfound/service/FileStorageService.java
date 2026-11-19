package com.learn.lostfound.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String save(MultipartFile file) throws IOException;
    void delete(String imageUrl) throws IOException;
}
