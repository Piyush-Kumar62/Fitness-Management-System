package com.project.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
  private String id;
  private String fileName;
  private String fileType;
  private Long fileSize;
  private String fileUrl;
  private LocalDateTime uploadedAt;
}
