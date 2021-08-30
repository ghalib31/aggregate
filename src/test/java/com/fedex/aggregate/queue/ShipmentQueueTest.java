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
class ShipmentQueueTest extends ClientQueueTest {

  @InjectMocks
  private ShipmentQueue shipmentQueue;

  @Test
  void addToQueue() throws InterruptedException {
    shipmentQueue.addToQueue(getMockSet(), "1");
    assertNotEquals(0, shipmentQueue.oldestRequestTime);
  }

  @Test
  void checkStatus_accepted() {
    final ResponseEntity<Map<String, JsonNode>> response = shipmentQueue.checkStatus("1");
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
  }

  @Test
  void checkStatus_ok() {
    mockOkResponse(shipmentQueue);
    final ResponseEntity<Map<String, JsonNode>> response = shipmentQueue.checkStatus("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getResults_not_found() {
    final ResponseEntity<Map<String, JsonNode>> response = shipmentQueue.getResults("1");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getResults_ok() {
    mockOkResponse(shipmentQueue);
    final ResponseEntity<Map<String, JsonNode>> response = shipmentQueue.getResults("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}