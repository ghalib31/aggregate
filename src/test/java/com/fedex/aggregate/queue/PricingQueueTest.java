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
class PricingQueueTest extends ClientQueueTest {

  @InjectMocks
  private PricingQueue pricingQueue;

  @Test
  void addToQueue() throws InterruptedException {
    pricingQueue.addToQueue(getMockSet(), "1");
    assertNotEquals(0, pricingQueue.oldestRequestTime);
  }

  @Test
  void checkStatus_accepted() {
    final ResponseEntity<Map<String, JsonNode>> response = pricingQueue.checkStatus("1");
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
  }

  @Test
  void checkStatus_ok() {
    mockOkResponse(pricingQueue);
    final ResponseEntity<Map<String, JsonNode>> response = pricingQueue.checkStatus("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getResults_not_found() {
    final ResponseEntity<Map<String, JsonNode>> response = pricingQueue.getResults("1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getResults_ok() {
    mockOkResponse(pricingQueue);
    final ResponseEntity<Map<String, JsonNode>> response = pricingQueue.getResults("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}