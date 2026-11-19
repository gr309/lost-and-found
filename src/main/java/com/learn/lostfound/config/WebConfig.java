package com.learn.lostfound.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration 
public class WebConfig implements WebMvcConfigurer  {
    private final String uploadDir;

    public WebConfig(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override 
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadDir, "items").toAbsolutePath().normalize().toUri().toString();
        if (!uploadPath.endsWith("/")) uploadPath += "/";
        registry.addResourceHandler("/uploads/items/*.jpg").addResourceLocations(uploadPath);
    }
}
