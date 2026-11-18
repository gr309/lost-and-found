package com.learn.lostfound.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
public class LocalFileStorageService {
    
}

// @Service 
// public class LocalFileStorageService implements FileStorageService{
//     private final Path uploadPath = Paths.get("uploads/items").toAbsolutePath().normalize();


//     @Override 
//     public String save(MultipartFile file) throws IOException {
//         if(file == null || file.isEmpty()) {
//             throw new IllegalArgumentException("Image cannot be empty");
//         }       

//         if(file.getSize() > 5 * 1024 * 1024) {
//             throw new IllegalArgumentException("Image must be under 5MB"); 
//         }

//         String contentType = file.getContentType(); 

//         String extention = switch (contentType) {
//             case "image/jpeg" -> ".jpg";
//             case "image/png" -> ".png";
//             case "image/webp" -> ".webp"; 
            
//             default -> throw new IllegalArgumentException( "Only JPG, PNG and WEBP are allowed");
//         };
        

//         System.out.println("Uploaded filename: " + file.getOriginalFilename());
//         System.out.println("Content type: " + contentType);
//         System.out.println("File size: " + file.getSize());
        
//         Files.createDirectories(uploadPath); 
//         String filename = UUID.randomUUID() + extention; 
//         Path destination = uploadPath.resolve(filename); 
//         Files.copy(file.getInputStream(), destination); 
        
//         return "/uploads/items/" + filename; 
//     }
// }
