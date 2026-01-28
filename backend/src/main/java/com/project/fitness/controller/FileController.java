package com.project.fitness.controller;

import com.project.fitness.model.FileMetadata;
import com.project.fitness.repository.FileMetadataRepository;
import com.project.fitness.service.IFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for file operations.
 * Follows Single Responsibility Principle - handles only file-related HTTP requests.
 * Depends on IFileStorageService abstraction (Dependency Inversion Principle).
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final IFileStorageService fileStorageService;
    private final FileMetadataRepository fileMetadataRepository;

    /**
     * Upload file with category
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            Authentication authentication
    ) {
        try {
            String userId = authentication.getName();
            String fileId = fileStorageService.storeFile(file, category, userId);
            String fileUrl = fileStorageService.getFileUrl(fileId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("fileId", fileId);
            response.put("fileUrl", fileUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("message", "File uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download file by ID
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        try {
            byte[] fileData = fileStorageService.loadFile(fileId);
            FileMetadata metadata = fileMetadataRepository.findByIdAndDeletedFalse(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(metadata.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + metadata.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Failed to download file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get all files uploaded by current user
     */
    @GetMapping("/my-files")
    public ResponseEntity<?> getMyFiles(Authentication authentication) {
        String userId = authentication.getName();
        List<FileMetadata> files = fileMetadataRepository
                .findByUploadedByIdAndDeletedFalse(userId);
        
        return ResponseEntity.ok(files);
    }

    /**
     * Get user files by category
     */
    @GetMapping("/my-files/{category}")
    public ResponseEntity<?> getMyFilesByCategory(
            @PathVariable String category,
            Authentication authentication
    ) {
        try {
            String userId = authentication.getName();
            FileMetadata.FileCategory fileCategory = FileMetadata.FileCategory
                    .valueOf(category.toUpperCase());
            
            List<FileMetadata> files = fileMetadataRepository
                    .findByUploadedByIdAndCategoryAndDeletedFalse(userId, fileCategory);
            
            return ResponseEntity.ok(files);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid category: " + category));
        }
    }

    /**
     * Delete file by ID
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file"));
        }
    }
}
