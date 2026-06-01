package com.project.fitness.ai.config;

import com.project.fitness.ai.provider.AiProviderType;
import java.time.Duration;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
@Data
public class AiProperties {

  private AiProviderType provider = AiProviderType.GEMINI;
  private List<AiProviderType> providerOrder = List.of(AiProviderType.GEMINI, AiProviderType.OLLAMA);
  private Features features = new Features();
  private Chat chat = new Chat();
  private Prompt prompt = new Prompt();
  private PromptTemplates promptTemplates = new PromptTemplates();
  private Context context = new Context();
  private Gemini gemini = new Gemini();
  private Ollama ollama = new Ollama();
  private Http http = new Http();

  @Data
  public static class Features {
    private boolean chatbotEnabled = true;
    private boolean ragEnabled = false;
    private boolean toolCallingEnabled = false;
    private boolean memoryEnabled = true;
    private boolean auditEnabled = true;
  }

  @Data
  public static class Chat {
    private int maxHistoryMessages = 12;
    private int maxPromptChars = 4000;
    private java.time.Duration streamTimeout = java.time.Duration.ofMinutes(2);
  }

  @Data
  public static class Prompt {
    private String system = "You are a helpful fitness assistant. Keep answers concise and actionable.";
  }

  @Data
  public static class PromptTemplates {
    private String member = "You are a fitness assistant for members. Be practical, encouraging, and concise.";
    private String trainer = "You are a fitness assistant for trainers. Provide coaching-ready guidance.";
    private String owner = "You are a fitness assistant for gym owners. Focus on operations and metrics.";
    private String admin = "You are a fitness assistant for administrators. Focus on compliance and reporting.";
  }

  @Data
  public static class Context {
    private int summaryMaxChars = 1200;
    private int summaryTriggerMessages = 20;
  }

  @Data
  public static class Gemini {
    private String apiKey;
    private String model = "gemini-1.5-flash";
    private String baseUrl = "https://generativelanguage.googleapis.com";
  }

  @Data
  public static class Ollama {
    private String baseUrl = "http://localhost:11434";
    private String model = "llama3";
  }

  @Data
  public static class Http {
    private int connectTimeoutMs = 3000;
    private Duration responseTimeout = Duration.ofSeconds(30);
  }
}
