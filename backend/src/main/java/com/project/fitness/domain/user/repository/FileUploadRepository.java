package com.project.fitness.domain.user.repository;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.user.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {
  List<FileUpload> findByUser_Id(String userId);
  List<FileUpload> findByUser_IdAndFileType(String userId, String fileType);
}
