package com.project.fitness.service;

import com.project.fitness.config.FileStorageConfig;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.model.FileMetadata;
import com.project.fitness.model.User;
import com.project.fitness.repository.FileMetadataRepository;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Local file storage implementation.
 * Follows Single Responsibility Principle - handles only local file storage.
 * Follows Liskov Substitution Principle - can substitute IFileStorageService.
 * Implements IFileStorageService (Dependency Inversion Principle).
 */
@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements IFileStorageService {

    private final FileStorageConfig fileStorageConfig;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserRepository userRepository;

    @Override
    public String storeFile(MultipartFile file, String category, String userId) throws IOException {
        // Validate file
        validateFile(file);
        
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Generate unique filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + "." + extension;
        
        // Determine storage path based on category
        Path storageLocation = getStoragePath(category);
        
        // Create directories if they don't exist
        Files.createDirectories(storageLocation);
        
        // Store file
        Path targetLocation = storageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Create thumbnail for images
        String thumbnailPath = null;
        if (isImage(extension)) {
            thumbnailPath = createThumbnail(targetLocation, fileName);
        }
        
        // Save metadata
        FileMetadata metadata = FileMetadata.builder()
                .fileName(fileName)
                .originalFileName(originalFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .storageLocation("LOCAL")
                .filePath(targetLocation.toString())
                .thumbnailPath(thumbnailPath)
                .category(FileMetadata.FileCategory.valueOf(category.toUpperCase()))
                .uploadedBy(user)
                .uploadedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        
        metadata = fileMetadataRepository.save(metadata);
        
        log.info("File stored successfully: {} for user: {}", fileName, userId);
        
        return metadata.getId();
    }

    @Override
    public byte[] loadFile(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findByIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
        
        Path filePath = Paths.get(metadata.getFilePath());
        
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File", "path", metadata.getFilePath());
        }
        
        return Files.readAllBytes(filePath);
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findByIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
        
        // Soft delete in database
        metadata.setDeleted(true);
        metadata.setDeletedAt(LocalDateTime.now());
        fileMetadataRepository.save(metadata);
        
        // Delete physical file
        Path filePath = Paths.get(metadata.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Delete thumbnail if exists
        if (metadata.getThumbnailPath() != null) {
            Path thumbnailPath = Paths.get(metadata.getThumbnailPath());
            if (Files.exists(thumbnailPath)) {
                Files.delete(thumbnailPath);
            }
        }
        
        log.info("File deleted: {}", fileId);
    }

    @Override
    public String getFileUrl(String fileId) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
        
        return "/api/files/" + fileId;
    }

    @Override
    public Path getFilePath(String fileId) {
        FileMetadata metadata = fileMetadataRepository.findByIdAndDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
        
        return Paths.get(metadata.getFilePath());
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload empty file");
        }
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(fileName);
        
        if (!fileStorageConfig.isValidExtension(extension)) {
            throw new BadRequestException("File type not allowed: " + extension);
        }
        
        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new BadRequestException("File size exceeds maximum limit of " + 
                    (fileStorageConfig.getMaxFileSize() / 1024 / 1024) + "MB");
        }
    }

    /**
     * Get storage path based on category
     */
    private Path getStoragePath(String category) {
        return switch (category.toUpperCase()) {
            case "PROFILE_PICTURE" -> Paths.get(fileStorageConfig.getProfilePicturesPath());
            case "PROGRESS_PHOTO" -> Paths.get(fileStorageConfig.getProgressPhotosPath());
            default -> Paths.get(fileStorageConfig.getUploadDir());
        };
    }

    /**
     * Check if file is an image
     */
    private boolean isImage(String extension) {
        return extension.matches("(?i)(jpg|jpeg|png|gif|webp)");
    }

    /**
     * Create thumbnail for image
     */
    private String createThumbnail(Path originalPath, String fileName) throws IOException {
        try {
            String thumbnailFileName = "thumb_" + fileName;
            Path thumbnailPath = originalPath.getParent().resolve(thumbnailFileName);
            
            Thumbnails.of(originalPath.toFile())
                    .size(200, 200)
                    .outputQuality(0.85)
                    .toFile(thumbnailPath.toFile());
            
            return thumbnailPath.toString();
        } catch (Exception e) {
            log.error("Failed to create thumbnail for: {}", fileName, e);
            return null;
        }
    }
}
