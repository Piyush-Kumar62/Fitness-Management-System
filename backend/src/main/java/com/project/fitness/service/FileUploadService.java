package com.project.fitness.service;

import com.project.fitness.dto.FileUploadResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.model.FileUpload;
import com.project.fitness.model.User;
import com.project.fitness.repository.FileUploadRepository;
import com.project.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileUploadService {

  private final FileUploadRepository fileUploadRepository;
  private final UserRepository userRepository;

  @Value("${file.upload-dir:./uploads}")
  private String uploadDir;

  public FileUploadService(FileUploadRepository fileUploadRepository, UserRepository userRepository) {
    this.fileUploadRepository = fileUploadRepository;
    this.userRepository = userRepository;
  }

  public FileUploadResponse uploadFile(MultipartFile file, String userId) {
    if (file.isEmpty()) {
      throw new BadRequestException("File is empty");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    try {
      // Create upload directory if it doesn't exist
      Path uploadPath = Paths.get(uploadDir);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      // Generate unique filename
      String originalFilename = file.getOriginalFilename();
      String extension = originalFilename != null && originalFilename.contains(".")
          ? originalFilename.substring(originalFilename.lastIndexOf("."))
          : "";
      String uniqueFilename = UUID.randomUUID() + extension;

      // Save file
      Path filePath = uploadPath.resolve(uniqueFilename);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      // Save metadata to database
      FileUpload fileUpload = FileUpload.builder()
          .user(user)
          .fileName(originalFilename)
          .filePath(filePath.toString())
          .fileType(file.getContentType())
          .fileSize(file.getSize())
          .build();

      FileUpload saved = fileUploadRepository.save(fileUpload);
      return mapToResponse(saved);

    } catch (IOException e) {
      throw new RuntimeException("Failed to store file: " + e.getMessage());
    }
  }

  public FileUploadResponse getFileById(String fileId) {
    FileUpload fileUpload = fileUploadRepository.findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found"));
    return mapToResponse(fileUpload);
  }

  public List<FileUploadResponse> getUserFiles(String userId) {
    return fileUploadRepository.findByUser_Id(userId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public void deleteFile(String fileId, String userId) {
    FileUpload fileUpload = fileUploadRepository.findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found"));

    if (!fileUpload.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized to delete this file");
    }

    try {
      // Delete physical file
      Path filePath = Paths.get(fileUpload.getFilePath());
      Files.deleteIfExists(filePath);

      // Delete from database
      fileUploadRepository.delete(fileUpload);
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete file: " + e.getMessage());
    }
  }

  private FileUploadResponse mapToResponse(FileUpload fileUpload) {
    return new FileUploadResponse(
        fileUpload.getId(),
        fileUpload.getFileName(),
        fileUpload.getFileType(),
        fileUpload.getFileSize(),
        "/api/files/" + fileUpload.getId(),
        fileUpload.getUploadedAt()
    );
  }
}
