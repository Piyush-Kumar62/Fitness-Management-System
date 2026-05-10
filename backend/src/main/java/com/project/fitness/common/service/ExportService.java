package com.project.fitness.common.service;

import com.project.fitness.domain.fitness.model.Activity;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

  public byte[] exportActivitiesToCsv(List<Activity> activities) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (PrintWriter pw = new PrintWriter(out)) {
      // Header
      pw.println("ID,Type,Duration(s),CaloriesBurned,Date");

      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

      for (Activity activity : activities) {
        pw.printf("%s,%s,%d,%d,%s%n",
            activity.getId(),
            activity.getType(),
            activity.getDuration(),
            activity.getCaloriesBurned(),
            activity.getCreatedAt() != null ? activity.getCreatedAt().format(formatter) : "");
      }
    }
    return out.toByteArray();
  }
}
