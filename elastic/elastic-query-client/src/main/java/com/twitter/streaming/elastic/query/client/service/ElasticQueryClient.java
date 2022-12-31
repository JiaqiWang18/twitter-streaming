package com.twitter.streaming.elastic.query.client.service;

import com.twitter.streaming.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticQueryClient <T extends IndexModel> {
    T getIndexModelById(String id);
    List<T> getIndexModelByText(String text);
    List<T> getAllIndexModels();
}

