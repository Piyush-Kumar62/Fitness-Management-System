package com.project.fitness.ai.audit;

import com.project.fitness.ai.audit.model.AiAuditLog;
import com.project.fitness.ai.audit.repository.AiAuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AiAuditService {

  private final AiAuditLogRepository repository;

  public AiAuditService(AiAuditLogRepository repository) {
    this.repository = repository;
  }

  public void save(AiAuditLog log) {
    repository.save(log);
  }
}
