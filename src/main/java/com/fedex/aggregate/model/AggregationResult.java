package com.fedex.aggregate.model;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * The data class Aggregation result.
 */
@Data
public class AggregationResult {

  private final Map<String, JsonNode> pricing;
  private final Map<String, JsonNode> track;
  private final Map<String, JsonNode> shipments;

}