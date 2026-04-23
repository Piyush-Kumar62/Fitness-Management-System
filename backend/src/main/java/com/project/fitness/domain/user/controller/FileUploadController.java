package com.project.fitness.domain.user.controller;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.user.dto.FileUploadResponse;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.domain.user.model.FileUpload;
import com.project.fitness.modules.user.application.UserApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

  private final UserApplicationService userApplicationService;

  public FileUploadController(UserApplicationService userApplicationService) {
    this.userApplicationService = userApplicationService;
  }

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    FileUploadResponse response = userApplicationService.uploadFile(file, userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Resource> getFile(
      @PathVariable String id,
      Authentication authentication) {
    try {
      String userId = (String) authentication.getPrincipal();
      FileUpload fileUpload = userApplicationService.getFileEntity(id);
      if (!isAdmin(authentication) && !fileUpload.getUser().getId().equals(userId)) {
        throw new UnauthorizedException("Unauthorized to access this file");
      }

      Path filePath = Paths.get(fileUpload.getFilePath());
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileUpload.getFileType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileUpload.getFileName() + "\"")
            .body(resource);
      } else {
        throw new RuntimeException("Could not read file");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving file: " + e.getMessage());
    }
  }

  @GetMapping("/user/me")
  public ResponseEntity<List<FileUploadResponse>> getMyFiles(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    List<FileUploadResponse> files = userApplicationService.getUserFiles(userId);
    return ResponseEntity.ok(files);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    userApplicationService.deleteFile(id, userId);
    return ResponseEntity.noContent().build();
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
  }
}
