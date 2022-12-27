package com.twitter.streaming.twitter.to.kafka.service.exception;

public class TwitterToKafkaServiceException extends RuntimeException {
  // default constructor
  public TwitterToKafkaServiceException() {
    super();
  }
  public TwitterToKafkaServiceException(String message) {
    super(message);
  }

  public TwitterToKafkaServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}

