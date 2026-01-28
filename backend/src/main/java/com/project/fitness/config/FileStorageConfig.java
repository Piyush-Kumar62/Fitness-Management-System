package com.project.fitness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for file storage.
 * Follows Single Responsibility Principle - handles only file storage configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "app.file")
@Data
public class FileStorageConfig {
    
    private String uploadDir;
    private String profilePicturesDir;
    private String progressPhotosDir;
    private List<String> allowedExtensions;
    private long maxFileSize;
    
    /**
     * Check if file extension is allowed
     */
    public boolean isValidExtension(String extension) {
        return allowedExtensions != null && 
               allowedExtensions.contains(extension.toLowerCase());
    }
    
    /**
     * Get full path for profile pictures directory
     */
    public String getProfilePicturesPath() {
        return uploadDir + "/" + profilePicturesDir;
    }
    
    /**
     * Get full path for progress photos directory
     */
    public String getProgressPhotosPath() {
        return uploadDir + "/" + progressPhotosDir;
    }
}
