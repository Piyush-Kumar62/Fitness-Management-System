package com.project.fitness.controller;

import com.project.fitness.dto.FileUploadResponse;
import com.project.fitness.service.FileUploadService;
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
@RequestMapping("/api/files")
public class FileUploadController {

  private final FileUploadService fileUploadService;

  public FileUploadController(FileUploadService fileUploadService) {
    this.fileUploadService = fileUploadService;
  }

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    FileUploadResponse response = fileUploadService.uploadFile(file, userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Resource> getFile(@PathVariable String id) {
    try {
      FileUploadResponse fileInfo = fileUploadService.getFileById(id);
      Path filePath = Paths.get(fileInfo.getFileUrl().replace("/api/files/", ""));
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileInfo.getFileName() + "\"")
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
    List<FileUploadResponse> files = fileUploadService.getUserFiles(userId);
    return ResponseEntity.ok(files);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    fileUploadService.deleteFile(id, userId);
    return ResponseEntity.noContent().build();
  }
}
