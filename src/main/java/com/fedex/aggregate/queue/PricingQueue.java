package com.fedex.aggregate.queue;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fedex.aggregate.properties.AggregateApiProperties;

/**
 * Pricing queue.
 */
@Component
public class PricingQueue extends ClientQueue {
  /**
   * Instantiates a new Pricing queue.
   *
   * @param restTemplate           the rest template
   * @param aggregateApiProperties the aggregate api properties
   */
  public PricingQueue(final RestTemplate restTemplate, final AggregateApiProperties aggregateApiProperties) {
    super(restTemplate, aggregateApiProperties.getPricingUrl());
  }
}
