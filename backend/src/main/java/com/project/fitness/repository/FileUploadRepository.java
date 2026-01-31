package com.project.fitness.repository;

import com.project.fitness.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {
  List<FileUpload> findByUser_Id(String userId);
  List<FileUpload> findByUser_IdAndFileType(String userId, String fileType);
}
