package com.project.fitness.ai.chat.service;

import com.project.fitness.ai.chat.entity.AiTokenUsage;
import com.project.fitness.ai.chat.repository.AiTokenUsageRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class AiTokenUsageService {

  private final AiTokenUsageRepository repository;

  public AiTokenUsageService(AiTokenUsageRepository repository) {
    this.repository = repository;
  }

  public void recordUsage(String userId, String provider, Integer promptTokens, Integer completionTokens) {
    Integer totalTokens = safeSum(promptTokens, completionTokens);
    AiTokenUsage usage = AiTokenUsage.builder()
        .userId(userId)
        .provider(provider)
        .promptTokens(promptTokens)
        .completionTokens(completionTokens)
        .totalTokens(totalTokens)
        .costEstimate(estimateCost(provider, totalTokens))
        .build();
    repository.save(usage);
  }

  public int estimateTokens(String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }
    return Math.max(1, text.length() / 4);
  }

  private Integer safeSum(Integer a, Integer b) {
    int first = a == null ? 0 : a;
    int second = b == null ? 0 : b;
    return first + second;
  }

  private BigDecimal estimateCost(String provider, Integer totalTokens) {
    if (totalTokens == null || totalTokens == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.ZERO;
  }
}
