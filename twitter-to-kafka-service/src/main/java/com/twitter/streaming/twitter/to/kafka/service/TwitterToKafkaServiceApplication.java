package com.twitter.streaming.twitter.to.kafka.service;

import com.twitter.streaming.config.TwitterToKafkaServiceConfigData;
import com.twitter.streaming.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming") // This is needed to scan the config package
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
  private final StreamRunner streamRunner;

  public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, StreamRunner streamRunner) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.streamRunner = streamRunner;
  }

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // used to run a code block only once in application’s lifetime – after application is initialized.
    LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
    LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
    streamRunner.start(); // start streaming tweets
  }

}
