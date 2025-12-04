package com.example.learning_management_system.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve the absolute path to the "uploads" folder
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String filePath = "file:" + uploadPath + "/";

        System.out.println("Serving files from: " + filePath);

        // Map /uploads/** URLs to physical uploads folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(filePath);
    }
}


