package com.fedex.aggregate.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fedex.aggregate.model.QueueRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for queue.
 */
@EnableScheduling
@Slf4j
public abstract class ClientQueue {
  private static final int QUEUE_EXPIRE_TIME = 5000;
  private final RestTemplate restTemplate;
  private final String url;
  private final Map<String, ResponseEntity<Map<String, JsonNode>>> enrichedResponses = new HashMap<>();
  protected BlockingQueue<QueueRequest> queue = new ArrayBlockingQueue<>(5);
  protected long oldestRequestTime = 0;

  /**
   * Instantiates a new Client queue.
   *
   * @param restTemplate the restTemplate
   * @param url       the url
   */
  protected ClientQueue(final RestTemplate restTemplate, final String url) {
    this.restTemplate = restTemplate;
    this.url = url;
  }

  /**
   * Add to queue.
   *
   * @param queryParams the query params
   * @param id          the id
   */
  public void addToQueue(final Set<String> queryParams, final String id) throws InterruptedException {
    for (String queryParam : queryParams) {
      queue.offer(QueueRequest.builder().id(id).requestParam(queryParam).build(), 1, TimeUnit.SECONDS);
      if (queue.size() == 1) {
        oldestRequestTime = System.currentTimeMillis();
      }
      if (queue.remainingCapacity() == 0) {
        executeRequests();
      }
    }

  }

  /**
   * Check status response entity.
   *
   * @param id the id
   * @return the response entity
   */
  public ResponseEntity<Map<String, JsonNode>> checkStatus(final String id) {
    final ResponseEntity<Map<String, JsonNode>> response = enrichedResponses.get(id);
    if (response == null) {
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Gets results.
   *
   * @param id the id
   * @return the results
   */
  public ResponseEntity<Map<String, JsonNode>> getResults(final String id) {
    final ResponseEntity<Map<String, JsonNode>> response = enrichedResponses.get(id);
    if (response == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    enrichedResponses.remove(id);
    return response;
  }

  @Scheduled(fixedRate = 1000)
  private void respondToOldRequests() {
    if (oldestRequestTime != 0 && System.currentTimeMillis() - oldestRequestTime >= QUEUE_EXPIRE_TIME && queue.size() > 0) {
      executeRequests();
      oldestRequestTime = 0;
    }
  }

  private void executeRequests() {
    log.info("Requesting thread to run");
    new Requester(queue, url, restTemplate, enrichedResponses).run();
  }

}
