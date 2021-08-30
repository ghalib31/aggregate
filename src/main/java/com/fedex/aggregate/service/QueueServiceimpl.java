package com.fedex.aggregate.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fedex.aggregate.model.AggregationResult;
import com.fedex.aggregate.queue.PricingQueue;
import com.fedex.aggregate.queue.ShipmentQueue;
import com.fedex.aggregate.queue.TrackingQueue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Queue service to place request in queue and get response.
 */
@Service
@Slf4j
@AllArgsConstructor
public class QueueServiceimpl implements QueueService{

  private final ShipmentQueue shipmentQueue;
  private final PricingQueue pricingQueue;
  private final TrackingQueue trackingQueue;
  private final List<String> shipmentNoArgsList;
  private final List<String> pricingNoArgsList;
  private final List<String> trackingNoArgsList;

  /**
   * Add to queue.
   *
   * @param pricing   the pricing
   * @param track     the track
   * @param shipments the shipments
   * @return the response entity
   */
  public ResponseEntity<String> addToQueue(final Optional<Set<String>> pricing, final Optional<Set<String>> track,
                                           final Optional<Set<String>> shipments) throws InterruptedException {
    log.info("Adding to queue");
    final String id = UUID.randomUUID().toString();
    if (pricing.isPresent()) {
      pricingQueue.addToQueue(pricing.get(), id);
    } else {
      pricingNoArgsList.add(id);
    }
    if (track.isPresent()) {
      trackingQueue.addToQueue(track.get(), id);
    } else {
      trackingNoArgsList.add(id);
    }
    if (shipments.isPresent()) {
      shipmentQueue.addToQueue(shipments.get(), id);
    } else {
      shipmentNoArgsList.add(id);
    }
    return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/aggregation/checkStatus/" + id)).build();
  }

  /**
   * Check status of response.
   *
   * @param id the id
   * @return the response entity
   * @throws InterruptedException the interrupted exception
   */
  public ResponseEntity<String> checkStatus(final String id) throws InterruptedException {
    log.info("Checking status of request {}", id);
    if (isReady(id)) {
      return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/aggregation/getResults/" + id)).build();
    }
    Thread.sleep(1000);
    return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/aggregation/checkStatus/" + id)).build();
  }

  private boolean isReady(final String id) {
    boolean shipmentStatus = shipmentNoArgsList.contains(id) || !HttpStatus.ACCEPTED.equals(shipmentQueue.checkStatus(id).getStatusCode());
    boolean trackingStatus = trackingNoArgsList.contains(id) || !HttpStatus.ACCEPTED.equals(trackingQueue.checkStatus(id).getStatusCode());
    boolean pricingStatus = pricingNoArgsList.contains(id) || !HttpStatus.ACCEPTED.equals(pricingQueue.checkStatus(id).getStatusCode());
    return shipmentStatus && trackingStatus && pricingStatus;
  }

  /**
   * Gets aggregated response.
   *
   * @param id the id
   * @return the aggregated response
   */
  public ResponseEntity<AggregationResult> getAggregatedResponse(final String id) {
    log.info("Fetching results for {}", id);
    final Map<String, JsonNode> pricingResponse = pricingQueue.getResults(id).getBody();
    final Map<String, JsonNode> trackingResponse = trackingQueue.getResults(id).getBody();
    final Map<String, JsonNode> shipmentResponse = shipmentQueue.getResults(id).getBody();

    final AggregationResult aggregationResult = new AggregationResult(pricingResponse, trackingResponse, shipmentResponse);
    final ResponseEntity<AggregationResult> aggregatedResponse = new ResponseEntity<>(aggregationResult, HttpStatus.OK);
    log.info("Aggregated response is {}", aggregatedResponse);
    return aggregatedResponse;
  }

}
