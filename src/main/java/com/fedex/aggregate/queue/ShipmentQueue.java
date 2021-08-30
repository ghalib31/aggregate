package com.fedex.aggregate.queue;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fedex.aggregate.properties.AggregateApiProperties;

/**
 * Shipment queue.
 */
@Component
public class ShipmentQueue extends ClientQueue {
  /**
   * Instantiates a new Shipment queue.
   *
   * @param restTemplate           the rest template
   * @param aggregateApiProperties the aggregate api properties
   */
  public ShipmentQueue(final RestTemplate restTemplate, final AggregateApiProperties aggregateApiProperties) {
    super(restTemplate, aggregateApiProperties.getShipmentsUrl());
  }
}
