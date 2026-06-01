package com.project.fitness.ai.websocket.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class AiStreamingMetricsService {

  private final MeterRegistry meterRegistry;
  private final AtomicInteger activeSessions = new AtomicInteger(0);

  public AiStreamingMetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    meterRegistry.gauge("ai.websocket.active.sessions", activeSessions);
  }

  public void onConnect() {
    meterRegistry.counter("ai.websocket.connections").increment();
    activeSessions.incrementAndGet();
  }

  public void onDisconnect() {
    activeSessions.updateAndGet(value -> Math.max(0, value - 1));
  }

  public void onMessageReceived() {
    meterRegistry.counter("ai.websocket.messages.received").increment();
  }

  public void onChunkSent() {
    meterRegistry.counter("ai.websocket.chunks.sent").increment();
  }

  public void onStreamCompleted() {
    meterRegistry.counter("ai.websocket.stream.completed").increment();
  }

  public void onStreamFailed() {
    meterRegistry.counter("ai.websocket.stream.failed").increment();
  }

  public Timer.Sample startStreamTimer() {
    return Timer.start(meterRegistry);
  }

  public void recordStreamDuration(Timer.Sample sample) {
    sample.stop(Timer.builder("ai.websocket.stream.duration").register(meterRegistry));
  }
}
