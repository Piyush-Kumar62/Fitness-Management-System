package com.project.fitness.common.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

// Paginated response wrapper for list endpoints. Wraps Spring's {@link Page} into a serializable DTO.
@Getter
@Builder
public class PagedResponse<T> {

  private final List<T> content;
  private final int page;
  private final int size;
  private final long totalElements;
  private final int totalPages;
  private final boolean last;
  private final boolean first;

  // Build from a Spring Data {@link Page}.
  public static <T> PagedResponse<T> from(Page<T> page) {
    return PagedResponse.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .first(page.isFirst())
        .last(page.isLast())
        .build();
  }
}
