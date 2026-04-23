package com.project.fitness.common.response;

import com.project.fitness.common.util.CorrelationIdUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

// Ensures v1 JSON APIs follow a consistent envelope while preserving non-JSON/file responses.
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    if (!(request instanceof ServletServerHttpRequest servletRequest)
        || !(response instanceof ServletServerHttpResponse servletResponse)) {
      return body;
    }

    String path = servletRequest.getServletRequest().getRequestURI();
    HttpStatus status = HttpStatus.resolve(servletResponse.getServletResponse().getStatus());

    if (path == null || !path.startsWith("/api/v1")) {
      return body;
    }
    if (status == HttpStatus.NO_CONTENT) {
      return body;
    }
    if (body == null) {
      return ApiResponse.<Void>builder()
          .success(true)
          .message("OK")
          .timestamp(java.time.Instant.now().toString())
          .correlationId(CorrelationIdUtil.get())
          .build();
    }
    if (body instanceof ApiResponse<?> || body instanceof Resource || body instanceof byte[]) {
      return body;
    }
    if (body instanceof String) {
      return body;
    }
    return ApiResponse.builder()
        .success(true)
        .message("OK")
        .data(body)
        .timestamp(java.time.Instant.now().toString())
        .correlationId(CorrelationIdUtil.get())
        .build();
  }
}
