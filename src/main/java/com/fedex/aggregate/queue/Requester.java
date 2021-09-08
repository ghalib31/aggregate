package com.fedex.aggregate.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.fedex.aggregate.model.QueueRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Requester class to make backend api calls.
 */
@AllArgsConstructor
@Slf4j
public class Requester implements Runnable {
  private final BlockingQueue<QueueRequest> queue;
  private final String url;
  private final RestTemplate restTemplate;
  private final Map<String, ResponseEntity<Map<String, JsonNode>>> enrichedResponses;

  @Override
  public void run() {
    final List<QueueRequest> requests = new ArrayList<>();
    queue.drainTo(requests);

    // Map request id with request parameters
    final Set<String> requestIdList = requests.stream().map(QueueRequest::getId).collect(Collectors.toSet());
    final Map<String, Set<String>> requestMap = new HashMap<>();
    for (String requestId : requestIdList) {
      requestMap.put(requestId,
          requests.stream()
              .filter(queueRequest -> queueRequest.getId().equalsIgnoreCase(requestId))
              .map(QueueRequest::getRequestParam)
              .collect(Collectors.toSet()));
    }

    // Convert parameters to a comma separated values
    final String csvParams = requests.stream().map(QueueRequest::getRequestParam).collect(Collectors.joining(","));
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
    mapResponse(requestMap, response);
  }

  private void mapResponse(final Map<String, Set<String>> requestMap, final ResponseEntity<JsonNode> apiResponse) {
    requestMap.forEach((key, value) -> {
      final Map<String, JsonNode> paramHashmap = new HashMap<>();
      value.forEach(param -> paramHashmap.put(param, apiResponse.getBody().get(param)));
      enrichedResponses.put(key, ResponseEntity.ok(paramHashmap));
    });
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
