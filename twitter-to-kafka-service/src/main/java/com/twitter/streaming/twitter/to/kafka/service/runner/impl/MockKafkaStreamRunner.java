package com.twitter.streaming.twitter.to.kafka.service.runner.impl;

import com.twitter.streaming.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.twitter.streaming.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.twitter.streaming.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.twitter.streaming.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {
  private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private static final Random RANDOM = new Random();

  private static final String[] WORDS = {"foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "waldo", "fred", "plugh", "xyzzy", "thud"};

  private static final  String tweetAsRawJson = "{" +
          "\"created_at\":\"{0}\"," +
          "\"id\":\"{1}\"," +
          "\"text\":\"{2}\"," +
          "\"user\":{\"id\":\"{3}\"}" +
          "}";

  private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

  public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterKafkaStatusListener twitterKafkaStatusListener) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
  }

  public void start() throws TwitterException {
    LOG.info("Starting mock stream");
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
    int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
    long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
    simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
  }

  private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
    // Simulate a twitter stream by generating random tweets
    Executors.newSingleThreadExecutor().submit(() ->{
      try {
        while (true) {
          String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
          Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
          twitterKafkaStatusListener.onStatus(status);
          sleep(sleepTimeMs);
        }
      } catch (TwitterException e) {
        throw new TwitterToKafkaServiceException("Error creating mock tweet", e);
      }
    });
  }

  private void sleep(long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      LOG.error("Error sleeping", e);
      throw new TwitterToKafkaServiceException("Error sleeping", e);
    }
  }

  private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
    String[] params = new String[] {
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_DATE_FORMAT, Locale.ENGLISH)),
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
            getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
    };
    return formatTweetAsJson(params);
  }

  private String formatTweetAsJson(String[] params) {
    String tweet = tweetAsRawJson;
    for (int i = 0; i < params.length; i++) {
      tweet = tweet.replace("{" + i + "}", params[i]);
    }
    return tweet;
  }

  private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
    int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
    StringBuilder sb = new StringBuilder();
    constructRandomTweet(keywords, tweetLength, sb);
    return sb.toString();
  }

  private void constructRandomTweet(String[] keywords, int tweetLength, StringBuilder sb) {
    for (int i = 0; i < tweetLength; i++) {
      sb.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
      if (i == tweetLength / 2) {
        sb.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
      }
    }
  }
}
