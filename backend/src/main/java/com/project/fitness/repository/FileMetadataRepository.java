package com.project.fitness.repository;

import com.project.fitness.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FileMetadata entity.
 * Follows Interface Segregation Principle - focused query methods.
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, String> {
    
    /**
     * Find all non-deleted files by user ID
     */
    List<FileMetadata> findByUploadedByIdAndDeletedFalse(String userId);
    
    /**
     * Find non-deleted files by user and category
     */
    List<FileMetadata> findByUploadedByIdAndCategoryAndDeletedFalse(
            String userId, 
            FileMetadata.FileCategory category
    );
    
    /**
     * Find non-deleted file by ID
     */
    Optional<FileMetadata> findByIdAndDeletedFalse(String id);
}
