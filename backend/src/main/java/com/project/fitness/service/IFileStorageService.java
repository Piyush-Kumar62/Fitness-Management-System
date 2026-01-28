package com.project.fitness.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * File storage service interface.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 * Follows Interface Segregation Principle - focused interface for file operations.
 * Follows Open/Closed Principle - can be extended with new implementations (S3, Azure, etc.).
 */
public interface IFileStorageService {
    
    /**
     * Store uploaded file
     * @param file Multipart file to store
     * @param category File category (PROFILE_PICTURE, PROGRESS_PHOTO, etc.)
     * @param userId ID of user uploading the file
     * @return File ID
     * @throws IOException if file storage fails
     */
    String storeFile(MultipartFile file, String category, String userId) throws IOException;
    
    /**
     * Load file content by ID
     * @param fileId File ID
     * @return File content as byte array
     * @throws IOException if file loading fails
     */
    byte[] loadFile(String fileId) throws IOException;
    
    /**
     * Delete file by ID (soft delete)
     * @param fileId File ID
     * @throws IOException if file deletion fails
     */
    void deleteFile(String fileId) throws IOException;
    
    /**
     * Get file URL by ID
     * @param fileId File ID
     * @return File URL
     */
    String getFileUrl(String fileId);
    
    /**
     * Get file path by ID
     * @param fileId File ID
     * @return File path
     */
    Path getFilePath(String fileId);
}
