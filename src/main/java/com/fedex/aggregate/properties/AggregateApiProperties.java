package com.fedex.aggregate.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

/**
 * The Aggregate api properties class. Properties are picked up from aggregate.properties
 */
@Configuration
@ConfigurationProperties(prefix = "api")
@PropertySource("classpath:aggregate.properties")
@Getter
@Setter
public class AggregateApiProperties {
  private String pricingUrl;
  private String trackingUrl;
  private String shipmentsUrl;
  private String readTimeout;
  private String connectTimeout;
}
