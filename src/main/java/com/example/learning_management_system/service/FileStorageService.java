package com.example.learning_management_system.service;



import com.example.learning_management_system.entity.CourseContent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Data
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    public String storeFile(MultipartFile file, Long courseId, CourseContent.ContentType contentType) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());

        // Validate filename (no ../ etc.)
        if (original.contains("..")) {
            throw new IOException("Invalid path sequence in file name: " + original);
        }

        // Decide subfolder based on content type
        String subFolder;
        switch (contentType) {
            case IMAGE -> subFolder = "images";
            case VIDEO -> subFolder = "videos";
            case PDF -> subFolder = "documents";
            default -> throw new IOException("Unsupported content type");
        }

        // Create directory: uploads/<subFolder>/<courseId>/
        Path courseFolder = uploadDir.resolve(subFolder).resolve(String.valueOf(courseId));
        Files.createDirectories(courseFolder);

        // Target path (inside safe directory only)
        Path target = courseFolder.resolve(System.currentTimeMillis() + "_" + original).normalize();

        // Ensure file is within uploads folder (not outside)
        if (!target.startsWith(uploadDir)) {
            throw new IOException("Attempt to write outside upload directory!");
        }

        // Save file
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Return relative web path (example: /uploads/images/5/file.pdf)
        Path relative = uploadDir.relativize(target);
        return "/uploads/" + relative.toString().replace("\\", "/");
    }

    public boolean deleteFile(String path) {
        if (path == null) return false;
        try {
            // Ensure only deleting inside uploadDir
            Path filePath = uploadDir.resolve(path.replace("/uploads/", "")).normalize();
            if (!filePath.startsWith(uploadDir)) {
                throw new IOException("Invalid delete path");
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}

