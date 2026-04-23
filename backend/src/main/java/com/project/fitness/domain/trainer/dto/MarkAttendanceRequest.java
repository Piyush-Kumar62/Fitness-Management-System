package com.project.fitness.domain.trainer.dto;
import com.project.fitness.domain.trainer.model.Attendance;

import com.project.fitness.domain.trainer.model.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkAttendanceRequest {

  @NotBlank(message = "Class ID is required")
  private String classId;

  @NotBlank(message = "Member ID is required")
  private String memberId;

  @NotNull(message = "Attendance status is required")
  private AttendanceStatus status;
}
