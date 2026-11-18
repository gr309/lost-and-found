package com.learn.lostfound.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service 
public class RapidfileStorageService implements FileStorageService{
    private final HttpClient httpClient; 
    private final ObjectMapper objectMapper; 
    private final ImageProcessor imageProcessor; 

    @Value("${rapidfile.api-url}")
    private String apiUrl ; 

    @Value("${rapidfile.secret-key}")
    private String secretKey;
    
    public RapidfileStorageService(ObjectMapper objectMapper, ImageProcessor imageProcessor) {
        this.objectMapper = objectMapper; 
        this.imageProcessor = imageProcessor;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(); 
    }

    @Override 
    public String save(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image is required"
            );
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum image size is 5MB"
            );
        }
        String contentType = file.getContentType();
        
        String extension = switch (contentType == null ? "" : contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";

            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, PNG and WEBP are allowed");
        };

        String filename = UUID.randomUUID() + ".jpg";

        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);

        String uploadUrl = apiUrl + "?name=" + encodedFilename + "&acl=public";

        byte[] compressedImage = imageProcessor.process(file);

        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + secretKey)
                    .header("Content-Type",  "image/jpeg")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(compressedImage))
                    .build(); 

        
        try {
            HttpResponse <String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); 
            if(response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image storage service rejected the upload");
            }

            JsonNode json = objectMapper.readTree(response.body());
            String imageUrl = json.path("file").path("url").asText(null); 

            if(imageUrl == null || imageUrl.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image storage service returned no URL");
            }
            return imageUrl; 
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IOException("Image upload interrupted", e);        
        }

    }
}
