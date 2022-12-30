package com.twitter.streaming.kafka.to.elastic.service.consumer.impl;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.kafka.admin.client.KafkaAdminClient;
import com.twitter.streaming.kafka.avro.model.TwitterAvroModel;
import com.twitter.streaming.kafka.to.elastic.service.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  private final KafkaAdminClient kafkaAdminClient;

  private final KafkaConfigData kafkaConfigData;

  public TwitterKafkaConsumer(KafkaListenerEndpointRegistry listenerEndpointRegistry,
                              KafkaAdminClient adminClient,
                              KafkaConfigData configData) {
    this.kafkaListenerEndpointRegistry = listenerEndpointRegistry;
    this.kafkaAdminClient = adminClient;
    this.kafkaConfigData = configData;
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    LOG.info("Topics with name {} created", kafkaConfigData.getTopicName());
    // start listening to the topic
    kafkaListenerEndpointRegistry.getListenerContainer("twitterTopicListener").start();
  }

  @Override
  @KafkaListener(id = "twitterTopicListener", topics = "${kafka-config.topic-name}")
  public void receive(@Payload List<TwitterAvroModel> messages,
                      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                    "sending it to elastic: Thread id {}",
            messages.size(),
            keys.toString(),
            partitions.toString(),
            offsets.toString(),
            Thread.currentThread().getId());
  }
}