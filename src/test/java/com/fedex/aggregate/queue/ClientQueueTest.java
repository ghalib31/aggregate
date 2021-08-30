package com.fedex.aggregate.queue;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.fedex.aggregate.properties.AggregateApiProperties;

public abstract class ClientQueueTest {

  @Mock
  protected Map<String, ResponseEntity> enrichedResponses;
  @Mock
  private AggregateApiProperties aggregateApiProperties;

  protected Set<String> getMockSet() {
    final Set<String> set = new HashSet<>();
    set.add("key");
    return set;
  }

  protected void mockOkResponse(ClientQueue clientQueue) {
    Map<String, ResponseEntity> map = new HashMap<>();
    map.put("1", ResponseEntity.ok().build());
    ReflectionTestUtils.setField(clientQueue, "enrichedResponses", map);
  }

}