package com.twitter.streaming.reactive.elastic.query.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming")
public class ReactiveElasticQueryServiceApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ReactiveElasticQueryServiceApplication.class, args);
    }
}
