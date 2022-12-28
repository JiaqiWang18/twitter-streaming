package com.twitter.streaming.twitter.to.kafka.service.init.impl;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.kafka.admin.client.KafkaAdminClient;
import com.twitter.streaming.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(KafkaStreamInitializer.class);

  private final KafkaConfigData kafkaConfigData;

  private final KafkaAdminClient kafkaAdminClient;

  public KafkaStreamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaAdminClient = kafkaAdminClient;
  }

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    LOG.info("Topics with name {} created successfully!", kafkaConfigData.getTopicNamesToCreate());
  }
}
