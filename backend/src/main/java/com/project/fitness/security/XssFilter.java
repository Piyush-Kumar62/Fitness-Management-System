package com.project.fitness.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Filter that defensively wraps HTTP requests to sanitize XSS payloads.
@Component
public class XssFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    // Skip XSS filtering for multipart/form-data requests (e.g., file uploads)
    // as it can corrupt the binary stream or cause parts parsing errors.
    String contentType = request.getContentType();
    if (contentType != null && contentType.toLowerCase().startsWith("multipart/form-data")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Wrap the request to intercept and sanitize payload, headers, and params
    XssRequestWrapper wrappedRequest = new XssRequestWrapper(request);
    filterChain.doFilter(wrappedRequest, response);
  }
}
