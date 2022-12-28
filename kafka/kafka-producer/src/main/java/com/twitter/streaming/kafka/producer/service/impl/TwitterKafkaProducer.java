package com.twitter.streaming.kafka.producer.service.impl;

import com.twitter.streaming.kafka.avro.model.TwitterAvroModel;
import com.twitter.streaming.kafka.producer.service.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;


@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topic, Long key, TwitterAvroModel value) {
      LOG.info("Sending message to topic: {}, key: {}, value: {}", topic, key, value);
      ListenableFuture<SendResult<Long, TwitterAvroModel>> resultFuture = kafkaTemplate.send(topic, key, value);
      addCallback(topic, key, value, resultFuture);
    }

    @PreDestroy
    public void close() {
      if (kafkaTemplate != null) {
        LOG.info("Closing Kafka Producer!");
        kafkaTemplate.destroy();
      }
    }

    private void addCallback(String topic, Long key, TwitterAvroModel value, ListenableFuture<SendResult<Long, TwitterAvroModel>> resultFuture) {
      resultFuture.addCallback(new ListenableFutureCallback<>() {
        @Override
        public void onFailure(Throwable throwable) {
          LOG.error("Error sending message to topic: {}, key: {}, value: {}", topic, key, value, throwable);
        }

        @Override
        public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
          RecordMetadata recordMetadata = result.getRecordMetadata();
          LOG.debug("Message sent to topic: {}, partition: {}, offset: {}, timestamp: {}, at time: {}",
                  recordMetadata.topic(),
                  recordMetadata.partition(),
                  recordMetadata.offset(),
                  recordMetadata.timestamp(),
                  System.nanoTime()
          );
        }
      });
    }
}
