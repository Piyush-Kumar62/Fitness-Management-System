package com.project.fitness.ai.audit.repository;

import com.project.fitness.ai.audit.model.AiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestLogRepository extends JpaRepository<AiRequestLog, Long> {
}
