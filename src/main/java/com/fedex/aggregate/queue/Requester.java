package com.fedex.aggregate.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Requester class to make backend api calls.
 */
@AllArgsConstructor
@Slf4j
public class Requester implements Runnable {
  private final BlockingQueue<Map<String, Set<String>>> queue;
  private final String url;
  private final RestTemplate restTemplate;
  private final Map<String, ResponseEntity<Map<String, JsonNode>>> enrichedResponses;

  @Override
  public void run() {
    final List<Map<String, Set<String>>> requests = new ArrayList<>();
    queue.drainTo(requests);

    final Set<String> requestList = new HashSet<>();
    final List<String> originalRequestIds = new ArrayList<>();
    requests.forEach(request -> {
      request.values().forEach(requestList::addAll);
      originalRequestIds.addAll(request.keySet());
    });

    final String csvParams = requestList.stream().collect(Collectors.joining(","));
    final HttpEntity<?> entity = new HttpEntity<>(createHeaders());
    log.info("Requesting {}{}", url, csvParams);
    ResponseEntity<JsonNode> response;
    try {
      response = restTemplate.exchange((url + csvParams), HttpMethod.GET, entity, JsonNode.class);
    } catch (HttpServerErrorException e) {
      log.error("Service unavailable {}", e.getMessage());
      response = getNullForServiceUnavailable(csvParams);
    }
    log.info("Response is {}", response);
    mapResponse(requests, response);
  }

  private void mapResponse(List<Map<String, Set<String>>> requests, ResponseEntity<JsonNode> apiResponse) {
    requests.forEach(request -> request.entrySet().forEach(entry -> {
      Map<String, JsonNode> paramHashmap = new HashMap<>();
      entry.getValue().forEach(param -> paramHashmap.put(param, apiResponse.getBody().get(param)));
      enrichedResponses.put(entry.getKey(), ResponseEntity.ok(paramHashmap));
    }));
  }

  private HttpHeaders createHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private ResponseEntity<JsonNode> getNullForServiceUnavailable(final String csv) {
    final ObjectMapper objectMapper = new ObjectMapper();
    final Map<String, String> map = new HashMap<>();
    Arrays.stream(csv.split(",")).forEach(csvKey -> map.put(csvKey, null));
    return ResponseEntity.ok(objectMapper.valueToTree(map));
  }
}
