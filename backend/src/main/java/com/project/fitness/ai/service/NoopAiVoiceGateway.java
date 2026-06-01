package com.project.fitness.ai.service;

import org.springframework.stereotype.Service;

@Service
public class NoopAiVoiceGateway implements AiVoiceGateway {

  @Override
  public boolean isEnabled() {
    return false;
  }
}
