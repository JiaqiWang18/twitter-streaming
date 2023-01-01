package com.twitter.streaming.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.twitter.streaming")
public class ElasticQueryWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryWebClientApplication.class, args);
    }
}
