package com.fedex.aggregate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Pojo fo queue request.
 */
@Setter
@Getter
@Builder
public class QueueRequest {
  private String id;
  private String requestParam;
}
