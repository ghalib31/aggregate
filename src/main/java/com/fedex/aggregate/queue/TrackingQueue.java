package com.fedex.aggregate.queue;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fedex.aggregate.properties.AggregateApiProperties;

/**
 * Tracking queue.
 */
@Component
public class TrackingQueue extends ClientQueue {

  /**
   * Instantiates a new Tracking queue.
   *
   * @param restTemplate           the rest template
   * @param aggregateApiProperties the aggregate api properties
   */
  protected TrackingQueue(final RestTemplate restTemplate, final AggregateApiProperties aggregateApiProperties) {
    super(restTemplate, aggregateApiProperties.getTrackingUrl());
  }
}
