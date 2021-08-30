package com.fedex.aggregate.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
class RequesterTest {

  private BlockingQueue<Map<String, Set<String>>> queue;
  private Map<String, ResponseEntity<Map<String, JsonNode>>> enrichedResponses;

  private Requester requester;
  @Mock
  private RestTemplate restTemplate;

  @Test
  void run() {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.valueToTree("{\"key\":\"value\"}");
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class))).thenReturn(ResponseEntity.ok(jsonNode));
    final Map<String, Set<String>> map = new HashMap<>();
    final Set<String> set = new HashSet<>();
    set.add("key");
    map.put("1", set);
    queue = new ArrayBlockingQueue<>(5);
    queue.add(map);
    enrichedResponses = new HashMap<>();
    requester = new Requester(queue, "http://mockUrl", restTemplate, enrichedResponses);
    requester.run();
    assertEquals(HttpStatus.OK, enrichedResponses.get("1").getStatusCode());
  }
}
