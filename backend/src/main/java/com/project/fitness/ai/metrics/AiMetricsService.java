package com.project.fitness.ai.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class AiMetricsService {

  private final MeterRegistry meterRegistry;

  public AiMetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public Timer.Sample startRequest() {
    meterRegistry.counter("ai.requests.total").increment();
    return Timer.start(meterRegistry);
  }

  public void recordSuccess(String provider) {
    meterRegistry.counter("ai.requests.success").increment();
  }

  public long recordLatency(Timer.Sample sample, String provider) {
    Timer timer = Timer.builder("ai.provider.latency")
        .tag("provider", provider)
        .register(meterRegistry);
    return (long) sample.stop(timer) / 1_000_000;
  }

  public void recordFailure(String provider) {
    meterRegistry.counter("ai.requests.failed").increment();
    if (provider != null && !provider.isBlank()) {
      meterRegistry.counter("ai.provider.failed", "provider", provider).increment();
    }
  }

  public void recordTokens(String provider, int totalTokens) {
    meterRegistry.counter("ai.tokens.used", "provider", provider).increment(totalTokens);
  }

  public void recordToolCall(String tool) {
    meterRegistry.counter("ai.tool.calls", "tool", tool).increment();
  }

  public void recordToolSuccess(String tool) {
    meterRegistry.counter("ai.tool.success", "tool", tool).increment();
  }

  public void recordToolFailure(String tool) {
    meterRegistry.counter("ai.tool.failure", "tool", tool).increment();
  }

  public Timer.Sample startToolTimer() {
    return Timer.start(meterRegistry);
  }

  public void recordToolDuration(String tool, Timer.Sample sample) {
    Timer timer = Timer.builder("ai.tool.duration")
        .tag("tool", tool)
        .register(meterRegistry);
    sample.stop(timer);
  }
}
