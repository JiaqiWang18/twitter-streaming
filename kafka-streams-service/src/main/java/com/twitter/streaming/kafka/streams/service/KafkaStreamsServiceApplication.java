package com.twitter.streaming.kafka.streams.service;

import com.twitter.streaming.kafka.streams.service.init.StreamsInitializer;
import com.twitter.streaming.kafka.streams.service.runner.StreamsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.microservices.demo"})
public class KafkaStreamsServiceApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsServiceApplication.class);

	private final StreamsRunner<String, Long> streamsRunner;

	private final StreamsInitializer streamsInitializer;

	public KafkaStreamsServiceApplication(StreamsRunner<String, Long> runner,
										  StreamsInitializer initializer) {
		this.streamsRunner = runner;
		this.streamsInitializer = initializer;
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsServiceApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("App starts...");
		streamsInitializer.init();
		streamsRunner.start();
	}

}