package com.twitter.streaming.reactive.elastic.query.service.business.impl;

import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.twitter.streaming.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import com.twitter.streaming.reactive.elastic.query.service.business.ElasticQueryService;
import com.twitter.streaming.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;

    private final ElasticToResponseModelTransformer transformer;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient,
                                      ElasticToResponseModelTransformer transformer) {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.transformer = transformer;
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        return reactiveElasticQueryClient
                .getIndexModelByText(text)
                .map(transformer::getResponseModel);
    }
}

