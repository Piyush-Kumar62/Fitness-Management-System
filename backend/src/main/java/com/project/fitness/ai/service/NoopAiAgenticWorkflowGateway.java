package com.project.fitness.ai.service;

import org.springframework.stereotype.Service;

@Service
public class NoopAiAgenticWorkflowGateway implements AiAgenticWorkflowGateway {

  @Override
  public boolean isEnabled() {
    return false;
  }
}
