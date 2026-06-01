package com.project.fitness.ai.provider.router;

import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.provider.AiProvider;
import com.project.fitness.ai.provider.AiProviderRegistry;
import com.project.fitness.ai.service.port.ProviderPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderRouter implements ProviderPort {

  private final AiProviderRegistry providerRegistry;
  private final AiProperties properties;
  private final MeterRegistry meterRegistry;

  public ProviderRouter(AiProviderRegistry providerRegistry, AiProperties properties, MeterRegistry meterRegistry) {
    this.providerRegistry = providerRegistry;
    this.properties = properties;
    this.meterRegistry = meterRegistry;
  }

  @Override
  public ProviderResult chatWithFallback(String prompt) {
    List<AiProvider> providers = providerRegistry.getOrderedProviders();
    Exception last = null;
    for (AiProvider provider : providers) {
      Timer.Sample sample = Timer.start(meterRegistry);
      try {
        String reply = provider.chat(prompt);
        meterRegistry.counter("provider.calls", "provider", provider.getType().name().toLowerCase()).increment();
        sample.stop(Timer.builder("ai.latency")
            .tag("provider", provider.getType().name().toLowerCase())
            .register(meterRegistry));
        return new ProviderResult(provider.getType(), provider.getModel(), reply, "provider");
      } catch (Exception ex) {
        last = ex;
        meterRegistry.counter("provider.errors", "provider", provider.getType().name().toLowerCase()).increment();
        log.warn("Provider {} failed, trying fallback", provider.getType(), ex);
      }
    }

    meterRegistry.counter("ai.failures").increment();
    if (properties.getFeatures().isChatbotEnabled()) {
      return new ProviderResult(
          properties.getProvider(),
          "fallback",
          "AI service is temporarily unavailable. Please try again shortly.",
          "fallback");
    }
    if (last instanceof RuntimeException runtimeException) {
      throw runtimeException;
    }
    throw new IllegalStateException("No AI providers are available", last);
  }
}
