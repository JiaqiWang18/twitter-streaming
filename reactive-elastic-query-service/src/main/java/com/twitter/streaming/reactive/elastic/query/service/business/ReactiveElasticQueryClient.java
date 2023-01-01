package com.twitter.streaming.reactive.elastic.query.service.business;

import com.twitter.streaming.elastic.model.index.IndexModel;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import reactor.core.publisher.Flux;

public interface ReactiveElasticQueryClient <T extends IndexModel> {
    Flux<TwitterIndexModel>  getIndexModelByText(String text);
}
