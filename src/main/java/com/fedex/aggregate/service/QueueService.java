package com.fedex.aggregate.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.fedex.aggregate.model.AggregationResult;

public interface QueueService {
  ResponseEntity<String> addToQueue(final Optional<Set<String>> pricing, final Optional<Set<String>> track,
                                           final Optional<Set<String>> shipments) throws InterruptedException;

  ResponseEntity<String> checkStatus(final String id) throws InterruptedException;

  ResponseEntity<AggregationResult> getAggregatedResponse(final String id);
}
