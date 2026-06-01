package com.project.fitness.ai.chat.repository;

import com.project.fitness.ai.chat.entity.AiTokenUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiTokenUsageRepository extends JpaRepository<AiTokenUsage, Long> {
}
