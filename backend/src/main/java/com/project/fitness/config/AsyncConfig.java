package com.project.fitness.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

  @Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
  public ThreadPoolTaskExecutor applicationTaskExecutor(
      @Value("${spring.task.execution.pool.core-size:2}") int coreSize,
      @Value("${spring.task.execution.pool.max-size:5}") int maxSize,
      @Value("${spring.task.execution.pool.queue-capacity:50}") int queueCapacity,
      @Value("${spring.task.execution.thread-name-prefix:async-}") String threadNamePrefix
  ) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(coreSize);
    executor.setMaxPoolSize(maxSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.setTaskDecorator(new MdcTaskDecorator());
    executor.initialize();
    return executor;
  }

  // Propagates request MDC (including correlationId) from caller to async thread.
  static class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      return () -> {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          } else {
            MDC.clear();
          }
          runnable.run();
        } finally {
          if (previous != null) {
            MDC.setContextMap(previous);
          } else {
            MDC.clear();
          }
        }
      };
    }
  }
}
