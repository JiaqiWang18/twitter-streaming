package com.twitter.streaming.reactive.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming")
public class ReactiveElasticQueryWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReactiveElasticQueryWebClientApplication.class, args);
    }
}
