package com.twitter.streaming.twitter.to.kafka.service;

import com.twitter.streaming.config.TwitterToKafkaServiceConfigData;
import com.twitter.streaming.twitter.to.kafka.service.init.StreamInitializer;
import com.twitter.streaming.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming") // This is needed to scan the config package
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
  private final StreamRunner streamRunner;
  private final StreamInitializer streamInitializer;

  public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, StreamRunner streamRunner, StreamInitializer streamInitializer) {
    this.streamRunner = streamRunner;
    this.streamInitializer = streamInitializer;
  }

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // used to run a code block only once in application’s lifetime – after application is initialized.
    streamInitializer.init(); // create topics
    streamRunner.start(); // start streaming tweets
  }

}
