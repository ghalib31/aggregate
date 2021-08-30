package com.fedex.aggregate.controller;

import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_AGGREGATION;
import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_CHECK_STATUS;
import static com.fedex.aggregate.constants.AggregateApiConstants.PAGE_GET_RESULTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fedex.aggregate.service.QueueServiceimpl;

@ExtendWith(SpringExtension.class)
class AggregateApiControllerTest {

  private MockMvc mockMvc;
  @Mock
  private QueueServiceimpl queueService;

  @InjectMocks
  private AggregateApiController controller;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void should_respond_bad_request_for_no_input_params() throws Exception {
    mockMvc.perform(get(PAGE_AGGREGATION)).andExpect(status().isBadRequest());
    verifyNoInteractions(queueService);
  }

  @Test
  void should_respond_okay_for_getAggregatedResponse() throws Exception {
    mockMvc.perform(get(PAGE_AGGREGATION + "?pricing=NL,CN&track=109347261,123456892&shipments=109347261,123456892"))
        .andExpect(status().isOk());
    verify(queueService, times(1)).addToQueue(any(), any(), any());
  }

  @Test
  void checkStatus() throws Exception {
    when(queueService.checkStatus("1")).thenReturn(new ResponseEntity<>(HttpStatus.SEE_OTHER));
    mockMvc.perform(get(PAGE_AGGREGATION + PAGE_CHECK_STATUS.replace("{id}", "1")))
        .andExpect(status().isSeeOther());
    verify(queueService, times(1)).checkStatus("1");
  }

  @Test
  void getResults() throws Exception {
    when(queueService.getAggregatedResponse("1")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    mockMvc.perform(get(PAGE_AGGREGATION + PAGE_GET_RESULTS.replace("{id}", "1")))
        .andExpect(status().isOk());
    verify(queueService, times(1)).getAggregatedResponse("1");
  }
}