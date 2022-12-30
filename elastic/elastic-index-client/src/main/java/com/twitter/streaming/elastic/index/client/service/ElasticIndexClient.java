package com.twitter.streaming.elastic.index.client.service;

import com.twitter.streaming.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient <T extends IndexModel> {
  List<String> save(List<T> documents);
}
