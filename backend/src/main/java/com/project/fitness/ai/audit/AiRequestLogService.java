package com.project.fitness.ai.audit;

import com.project.fitness.ai.audit.model.AiRequestLog;
import com.project.fitness.ai.audit.repository.AiRequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AiRequestLogService {

  private final AiRequestLogRepository repository;

  public AiRequestLogService(AiRequestLogRepository repository) {
    this.repository = repository;
  }

  public void save(AiRequestLog log) {
    repository.save(log);
  }
}
