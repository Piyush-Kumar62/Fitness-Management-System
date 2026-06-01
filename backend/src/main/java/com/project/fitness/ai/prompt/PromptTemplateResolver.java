package com.project.fitness.ai.prompt;

import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.domain.user.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplateResolver {

  private final AiProperties properties;

  public PromptTemplateResolver(AiProperties properties) {
    this.properties = properties;
  }

  public String resolve(UserRole role) {
    if (role == null) {
      return properties.getPrompt().getSystem();
    }
    return switch (role) {
      case ADMIN -> properties.getPromptTemplates().getAdmin();
      case OWNER -> properties.getPromptTemplates().getOwner();
      case TRAINER -> properties.getPromptTemplates().getTrainer();
      case MEMBER -> properties.getPromptTemplates().getMember();
    };
  }
}
