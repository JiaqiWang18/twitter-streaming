package com.twitter.streaming.twitter.to.kafka.service.listener;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.kafka.avro.model.TwitterAvroModel;
import com.twitter.streaming.kafka.producer.service.KafkaProducer;
import com.twitter.streaming.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

  private final KafkaConfigData kafkaConfigData;

  private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

  private final TwitterStatusToAvroTransformer transformer;

  public TwitterKafkaStatusListener(KafkaConfigData kafkaConfigData,
                                    KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                                    TwitterStatusToAvroTransformer transformer) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaProducer = kafkaProducer;
    this.transformer = transformer;
  }

  @Override
  public void onStatus(Status status) {
    // define what we do when seeing a new tweet
    LOG.info("Twitter status with text {} sending to kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
    TwitterAvroModel twitterAvroModel = transformer.getTwitterAvroModelFromStatus(status);
    // partition by user id, so that all tweets from a user are in the same partition and in order
    kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
  }
}
