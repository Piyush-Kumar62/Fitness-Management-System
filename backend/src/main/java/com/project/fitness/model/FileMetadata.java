package com.project.fitness.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to track file metadata in the database.
 * Follows Single Responsibility Principle - represents file metadata only.
 */
@Entity
@Table(name = "file_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private String storageLocation; // LOCAL or S3

    @Column(nullable = false, length = 1000)
    private String filePath;

    @Column(length = 500)
    private String s3Key;

    @Column(length = 1000)
    private String thumbnailPath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * File categories for different use cases
     */
    public enum FileCategory {
        PROFILE_PICTURE,
        PROGRESS_PHOTO,
        ACTIVITY_IMAGE,
        DOCUMENT
    }
}
