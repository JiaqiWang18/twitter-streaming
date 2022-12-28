package com.twitter.streaming.twitter.to.kafka.service.runner.impl;

import com.twitter.streaming.config.TwitterToKafkaServiceConfigData;
import com.twitter.streaming.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-v2-tweets} && not ${twitter-to-kafka-service.enable-mock-tweets}")
public class TwitterV2KafkaStreamRunner implements StreamRunner {

  private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  private final  TwitterV2StreamHelper twitterV2StreamHelper;

  public TwitterV2KafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterV2StreamHelper twitterV2StreamHelper) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterV2StreamHelper = twitterV2StreamHelper;
  }

  @Override
  public void start() {
    String bearerToken = twitterToKafkaServiceConfigData.getTwitterV2BearerToken();
    if (bearerToken != null) {
      try {
        twitterV2StreamHelper.setupRules(bearerToken, getRules());
        twitterV2StreamHelper.connectStream(bearerToken);
      } catch (IOException | URISyntaxException e) {
        LOG.error("Failed to set rules or connect to stream");
        throw new RuntimeException("Failed to set rules or connect to stream");
      }

    } else {
      LOG.error("Failed to load Bearer token");
      throw new RuntimeException("Failed to load Bearer token");
    }
  }

  private Map<String, String> getRules() {
    List<String> keywords = twitterToKafkaServiceConfigData.getTwitterKeywords();
    Map<String, String> rules = new HashMap<>();
    for (String keyword: keywords) {
      rules.put(keyword, "keyword: " + keyword);
    }
    LOG.info("Created filter for twitter stream for keywords: {}", keywords);
    return rules;
  }
}
