package com.project.fitness.config;

import com.project.fitness.common.util.CorrelationIdUtil;
import com.project.fitness.constants.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Inserts a correlation ID into MDC for every HTTP request. The ID is read from the X-Correlation-ID header or generated fresh. It is cleared after the response to prevent MDC leaks.
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    // Prefer client-supplied header, else generate short UUID
    String id = request.getHeader(AppConstants.CORRELATION_ID_HEADER);
    if (id == null || id.isBlank()) {
      id = UUID.randomUUID().toString().substring(0, 8);
    }

    CorrelationIdUtil.set(id);
    response.setHeader(AppConstants.CORRELATION_ID_HEADER, id); // Echo back so clients can trace

    try {
      chain.doFilter(request, response);
    } finally {
      CorrelationIdUtil.clear(); // Always clear to avoid thread-local leaks
    }
  }
}
