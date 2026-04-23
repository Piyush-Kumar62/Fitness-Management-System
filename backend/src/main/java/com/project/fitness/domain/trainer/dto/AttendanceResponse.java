package com.project.fitness.domain.trainer.dto;

import com.project.fitness.domain.trainer.model.AttendanceStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

  private String id;
  private String classId;
  private String memberId;
  private String memberName;
  private String markedBy;
  private LocalDate date;
  private AttendanceStatus status;
}
