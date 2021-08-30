package com.fedex.aggregate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fedex.aggregate.model.AggregationResult;
import com.fedex.aggregate.queue.PricingQueue;
import com.fedex.aggregate.queue.ShipmentQueue;
import com.fedex.aggregate.queue.TrackingQueue;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

  @Mock
  private ShipmentQueue shipmentQueue;
  @Mock
  private PricingQueue pricingQueue;
  @Mock
  private TrackingQueue trackingQueue;
  @Mock
  private List<String> shipmentNoArgsList;
  @Mock
  private List<String> pricingNoArgsList;
  @Mock
  private List<String> trackingNoArgsList;
  @InjectMocks
  private QueueServiceimpl queueService;

  @Test
  void addToQueue() throws InterruptedException {
    Optional<Set<String>> pricing = Optional.of(new HashSet<>());
    Optional<Set<String>> track = Optional.of(new HashSet<>());
    Optional<Set<String>> shipments = Optional.of(new HashSet<>());
    final ResponseEntity<String> response = queueService.addToQueue(pricing, track, shipments);
    assertEquals(response.getStatusCodeValue(), HttpStatus.SEE_OTHER.value());
    assertTrue(response.getHeaders().getLocation().getPath().startsWith("/aggregation/checkStatus/"));
  }

  @Test
  void checkStatus_wait() throws InterruptedException {
    mockCheckQueueStatus(ResponseEntity.accepted().build());
    final ResponseEntity<String> statusResponse = queueService.checkStatus("1");
    assertEquals(statusResponse.getStatusCodeValue(), HttpStatus.SEE_OTHER.value());
    assertTrue(statusResponse.getHeaders().getLocation().getPath().startsWith("/aggregation/checkStatus/"));
  }

  @Test
  void checkStatus_ok() throws InterruptedException {
    mockCheckQueueStatus(ResponseEntity.ok().build());
    final ResponseEntity<String> statusResponse = queueService.checkStatus("1");
    assertEquals(statusResponse.getStatusCodeValue(), HttpStatus.SEE_OTHER.value());
    assertTrue(statusResponse.getHeaders().getLocation().getPath().startsWith("/aggregation/getResults/"));
  }

  @Test
  void getResults() {
    final Map<String, JsonNode> mapResponse = new HashMap<>();
    ResponseEntity responseEntity = ResponseEntity.ok().body(mapResponse);

    when(shipmentQueue.getResults(anyString())).thenReturn(responseEntity);
    when(pricingQueue.getResults(anyString())).thenReturn(responseEntity);
    when(trackingQueue.getResults(anyString())).thenReturn(responseEntity);
    final ResponseEntity<AggregationResult> response = queueService.getAggregatedResponse("1");
    assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
  }

  private void mockCheckQueueStatus(ResponseEntity responseEntity) {
    when(shipmentQueue.checkStatus(anyString())).thenReturn(responseEntity);
    when(pricingQueue.checkStatus(anyString())).thenReturn(responseEntity);
    when(trackingQueue.checkStatus(anyString())).thenReturn(responseEntity);
  }
}