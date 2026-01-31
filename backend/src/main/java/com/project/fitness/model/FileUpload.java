package com.project.fitness.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_uploads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUpload {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String filePath;

  @Column(nullable = false)
  private String fileType;

  @Column(nullable = false)
  private Long fileSize;

  @CreationTimestamp
  private LocalDateTime uploadedAt;
}
