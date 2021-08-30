package com.fedex.aggregate.controller;

import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_AGGREGATION;
import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_CHECK_STATUS;
import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_GET_RESULTS;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fedex.aggregate.model.AggregationResult;
import com.fedex.aggregate.service.QueueServiceimpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Aggregate api controller class. The main controller which will expose endpoints to user
 */
@RestController
@RequestMapping(PAGE_AGGREGATION)
@AllArgsConstructor
@Slf4j
public class AggregateApiController {

  private final QueueServiceimpl queueService;

  /**
   * Endpoint for user to make requests. Requests will be placed in the queue.
   *
   * @param pricing   the pricing
   * @param track     the track
   * @param shipments the shipments
   * @return the aggregated response
   */
  @GetMapping
  public ResponseEntity<String> placeRequest(@RequestParam final Optional<Set<String>> pricing, @RequestParam final Optional<Set<String>> track,
                                             @RequestParam final Optional<Set<String>> shipments) throws InterruptedException {
    if (pricing.isPresent() || track.isPresent() || shipments.isPresent()) {
      return queueService.addToQueue(pricing, track, shipments);
    }
    return new ResponseEntity<>("No request parameter found", HttpStatus.BAD_REQUEST);
  }

  /**
   * Check the status of response.
   *
   * @param id the id
   * @return the response entity
   * @throws InterruptedException the interrupted exception
   */
  @GetMapping(PAGE_CHECK_STATUS)
  public ResponseEntity<String> checkStatus(@PathVariable final String id) throws InterruptedException {
    return queueService.checkStatus(id);
  }

  /**
   * Gets aggregated results.
   *
   * @param id the id
   * @return the results
   */
  @GetMapping(PAGE_GET_RESULTS)
  public ResponseEntity<AggregationResult> getAggregatedResponse(@PathVariable final String id) {
    return queueService.getAggregatedResponse(id);
  }
}
