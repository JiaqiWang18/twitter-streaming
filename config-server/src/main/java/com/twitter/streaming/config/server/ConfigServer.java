package com.twitter.streaming.config.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServer {
  public static void main(String[] args) {
    org.springframework.boot.SpringApplication.run(ConfigServer.class, args);
  }
}
