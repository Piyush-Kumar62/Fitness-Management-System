package com.project.fitness.ai.audit.repository;

import com.project.fitness.ai.audit.model.AiAuditLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAuditLogRepository extends JpaRepository<AiAuditLog, UUID> {
}
