package com.project.fitness.domain.trainer.dto;

import com.project.fitness.domain.trainer.model.ClassBookingStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassBookingResponse {

  private String id;
  private String classId;
  private String className;
  private String memberId;
  private String memberName;
  private ClassBookingStatus status;
  private LocalDateTime bookedAt;
}
