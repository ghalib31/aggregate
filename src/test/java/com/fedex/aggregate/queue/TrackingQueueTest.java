package com.fedex.aggregate.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
class TrackingQueueTest extends ClientQueueTest {

  @InjectMocks
  private TrackingQueue trackingQueue;

  @Test
  void addToQueue() throws InterruptedException {
    trackingQueue.addToQueue(getMockSet(), "1");
    assertNotEquals(0, trackingQueue.oldestRequestTime);
  }

  @Test
  void checkStatus_accepted() {
    final ResponseEntity<Map<String, JsonNode>> response = trackingQueue.checkStatus("1");
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
  }

  @Test
  void checkStatus_ok() {
    mockOkResponse(trackingQueue);
    final ResponseEntity<Map<String, JsonNode>> response = trackingQueue.checkStatus("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getResults_not_found() {
    final ResponseEntity<Map<String, JsonNode>> response = trackingQueue.getResults("1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getResults_ok() {
    mockOkResponse(trackingQueue);
    final ResponseEntity<Map<String, JsonNode>> response = trackingQueue.getResults("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}