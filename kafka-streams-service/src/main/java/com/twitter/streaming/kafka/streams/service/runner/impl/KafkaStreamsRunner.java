package com.twitter.streaming.kafka.streams.service.runner.impl;

import com.twitter.streaming.config.KafkaConfigData;
import com.twitter.streaming.config.KafkaStreamsConfigData;
import com.twitter.streaming.kafka.avro.model.TwitterAnalyticsAvroModel;
import com.twitter.streaming.kafka.avro.model.TwitterAvroModel;
import com.twitter.streaming.kafka.streams.service.config.KafkaStreamsConfig;
import com.twitter.streaming.kafka.streams.service.runner.StreamsRunner;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsRunner.class);

    private static final String REGEX = "\\w+";

    private final KafkaStreamsConfigData streamsConfigData;

    private final KafkaConfigData kafkaConfigData;

    private final Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    public KafkaStreamsRunner(KafkaStreamsConfigData streamsConfigData, KafkaConfigData kafkaConfigData,
                              @Qualifier("streamConfiguration") Properties streamsConfiguration) {
        this.streamsConfigData = streamsConfigData;
        this.kafkaConfigData = kafkaConfigData;
        this.streamsConfiguration = streamsConfiguration;
    }


    @Override
    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl()
        );

        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        final KStream<Long, TwitterAvroModel> twitterAvroModelStream = getTwitterAvroModelStream(serdeConfig, streamsBuilder);

        createTopology(twitterAvroModelStream, serdeConfig);

        startStreaming(streamsBuilder);
    }

    @PreDestroy
    public void close() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
        }
    }

    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        LOG.info("Topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
    }

    @Override
    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(
                                        streamsConfigData.getWordCountStoreName(),
                                        QueryableStoreTypes.keyValueStore()));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelStream, Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = getSerdeAnalyticsModel(serdeConfig);

        twitterAvroModelStream
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, word) -> word)
                .count(Materialized.as(streamsConfigData.getWordCountStoreName()))
                .toStream()
                .map(mapToAnalyticsModel())
                .to(streamsConfigData.getOutputTopicName(),
                        Produced.with(Serdes.String(), serdeTwitterAnalyticsAvroModel));
    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TwitterAnalyticsAvroModel>> mapToAnalyticsModel() {
        return (word, count) -> {
            LOG.info("sending to topic {}, word {} - count {}", streamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, TwitterAnalyticsAvroModel.newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAnalyticsAvroModel.configure(serdeConfig, false);
        return serdeTwitterAnalyticsAvroModel;
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvroModelStream(Map<String, String> serdeConfig, StreamsBuilder streamsBuilder) {
        final Serde<TwitterAvroModel> serdeTwitterAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAvroModel.configure(serdeConfig, false);
        return streamsBuilder.stream(streamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(),
                serdeTwitterAvroModel));
    }
}
