package com.twitter.streaming.twitter.to.kafka.service.transformer;

import com.twitter.streaming.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransformer {

  public TwitterAvroModel getTwitterAvroModelFromStatus(Status status) {
    return TwitterAvroModel.newBuilder()
        .setId(status.getId())
        .setCreatedAt(status.getCreatedAt().getTime())
        .setText(status.getText())
        .setUserId(status.getUser().getId())
        .build();
  }
}
