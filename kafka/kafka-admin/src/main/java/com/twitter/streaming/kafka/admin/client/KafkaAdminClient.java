package com.twitter.streaming.kafka.admin.client;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.config.RetryConfigData;
import com.twitter.streaming.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(KafkaAdminClient.class);

  private final KafkaConfigData kafkaConfigData;

  private final RetryConfigData retryConfigData;

  private final AdminClient adminClient;

  private final RetryTemplate retryTemplate;

  private final WebClient webClient;

  public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient, RetryTemplate retryTemplate, WebClient webClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.retryConfigData = retryConfigData;
    this.adminClient = adminClient;
    this.retryTemplate = retryTemplate;
    this.webClient = webClient;
  }

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      createTopicsResult = retryTemplate.execute(this::doCreateTopics); // returns a CreateTopicsResult future
      LOG.info("Create topics result: {}", createTopicsResult.values().values()); // blocks until the future is complete
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for creating kafka topics!", t);
    }
    checkTopicsCreated();
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();
    int retryCount = 1;
    Integer maxAttempts = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      while (!isTopicCreated(topics, topic)) {
        checkMaxRetry(retryCount, maxAttempts);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topics = getTopics();
      }
    }
  }

  public void checkSchemaRegistry() {
    Integer maxAttempts = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    while (!getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(maxAttempts, maxAttempts);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }


  private HttpStatus getSchemaRegistryStatus() {
    try{
      return webClient.get()
              .uri(kafkaConfigData.getSchemaRegistryUrl())
              .exchange()
              .map(ClientResponse::statusCode)
              .block();
    } catch (Exception e) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
    LOG.info("Creating {} topics, attempt {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
            topic.trim(),
            kafkaConfigData.getNumOfPartitions(),
            kafkaConfigData.getReplicationFactor())
    ).collect(Collectors.toList());
    return adminClient.createTopics(kafkaTopics);
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for getting kafka topics!", t);
    }
    return topics;
  }

  private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
    LOG.info("Getting topics {}, attempt {}", kafkaConfigData.getTopicNamesToCreate(),retryContext.getRetryCount());
    Collection<TopicListing> topics = adminClient.listTopics().listings().get();
    if (topics != null) {
      topics.forEach(topic -> LOG.debug("Topic: {}", topic.name()));
    }

    return topics;
  }


  private void checkMaxRetry(int retryCount, Integer maxAttempts) {
    if (retryCount > maxAttempts) {
      throw new KafkaClientException("Reached max number of retry for checking kafka topics!");
    }
  }

  private void sleep(Long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      LOG.error("Error while sleeping", e);
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
    if (topics == null) {
      return false;
    }
    return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));
  }
}
