package com.project.fitness.common.util;

import java.util.UUID;
import org.slf4j.MDC;

// Utility for MDC-based correlation ID management. Used by {@link com.project.fitness.config.CorrelationIdFilter} and the exception handler.
public final class CorrelationIdUtil {

  public static final String MDC_KEY = "correlationId";

  private CorrelationIdUtil() {}

  // Returns the current correlation ID from MDC, or generates a short UUID.
  public static String get() {
    String id = MDC.get(MDC_KEY);
    return (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString().substring(0, 8);
  }

  // Sets a new correlation ID in MDC.
  public static void set(String id) {
    MDC.put(MDC_KEY, id);
  }

  // Clears the correlation ID from MDC (call in finally block).
  public static void clear() {
    MDC.remove(MDC_KEY);
  }
}
