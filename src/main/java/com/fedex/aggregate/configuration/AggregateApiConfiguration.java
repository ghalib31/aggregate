package com.fedex.aggregate.configuration;

import static java.time.Duration.ofMillis;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fedex.aggregate.properties.AggregateApiProperties;
import lombok.AllArgsConstructor;

/**
 * The Aggregate api configuration class.
 */
@Configuration
@AllArgsConstructor
public class AggregateApiConfiguration {

  private final AggregateApiProperties aggregateApiProperties;

  /**
   * Create rest template. It will be used to make calls to backend apis.
   *
   * @param restTemplateBuilder the rest template builder
   * @return the rest template
   */
  @Bean
  public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(ofMillis(Long.valueOf(aggregateApiProperties.getConnectTimeout())))
        .setReadTimeout(ofMillis(Long.valueOf(aggregateApiProperties.getReadTimeout())))
        .build();
  }

}
