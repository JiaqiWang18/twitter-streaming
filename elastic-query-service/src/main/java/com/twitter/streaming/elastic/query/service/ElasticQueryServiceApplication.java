package com.twitter.streaming.elastic.query.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming")
public class ElasticQueryServiceApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ElasticQueryServiceApplication.class, args);
    }
}
