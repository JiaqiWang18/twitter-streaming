package com.twitter.streaming.reactive.elastic.query.service.business.impl;

import com.twitter.streaming.config.ElasticQueryServiceConfigData;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import com.twitter.streaming.reactive.elastic.query.service.repository.ElasticQueryRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class TwitterReactiveElasticQueryClient implements ReactiveElasticQueryClient<TwitterIndexModel> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterReactiveElasticQueryClient.class);

    private final ElasticQueryRepository elasticQueryRepository;

    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    public TwitterReactiveElasticQueryClient(ElasticQueryRepository elasticQueryRepository,
                                             ElasticQueryServiceConfigData elasticQueryServiceConfigData) {
        this.elasticQueryRepository = elasticQueryRepository;
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
    }

    @Override
    public Flux<TwitterIndexModel> getIndexModelByText(String text) {
        LOG.info("Querying ElasticSearch for text: {}", text);
        return elasticQueryRepository
                .findByText(text)
                .delayElements(Duration.ofMillis(elasticQueryServiceConfigData.getBackPressureDelayMs()));
    }
}
