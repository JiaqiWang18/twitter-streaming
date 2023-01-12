package com.twitter.streaming.kafka.streams.service.config;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.config.KafkaStreamsConfigData;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {
    private final KafkaConfigData kafkaConfigData;

    private final KafkaStreamsConfigData streamsConfigData;

    public KafkaStreamsConfig(KafkaConfigData kafkaConfigData, KafkaStreamsConfigData streamsConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.streamsConfigData = streamsConfigData;
    }

    @Bean
    @Qualifier("streamConfiguration")
    public Properties streamsConfiguration() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamsConfigData.getApplicationID());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaConfigData.getSchemaRegistryUrl());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, streamsConfigData.getStateFileLocation());
        return props;
    }
}
