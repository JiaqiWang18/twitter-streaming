package com.twitter.streaming.kafka.to.elastic.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.twitter.streaming")
@SpringBootApplication
public class KafkaToElasticServiceApplication {

    public static void main(String[] args) {
      org.springframework.boot.SpringApplication.run(KafkaToElasticServiceApplication.class, args);
    }
}
