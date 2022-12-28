package com.twitter.streaming.common.config;

import com.twitter.streaming.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {
  private final RetryConfigData retryConfigData;

  public RetryConfig(RetryConfigData configData) {
    this.retryConfigData = configData;
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
    backOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
    backOffPolicy.setMultiplier(retryConfigData.getMultiplier());

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());

    retryTemplate.setBackOffPolicy(backOffPolicy);
    retryTemplate.setRetryPolicy(retryPolicy);
    return retryTemplate;
  }
}
