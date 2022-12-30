package com.twitter.streaming.elastic.index.client.util;

import com.twitter.streaming.elastic.model.index.IndexModel;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticIndexUtil<T extends IndexModel> {
  /**
   * Convert a list of IndexModel to a list of IndexQuery
   *
   * @param documents list of IndexModel
   * @return list of IndexQuery
   */
  public List<IndexQuery> getIndexQueries(List<T> documents) {
    return documents.stream()
            .map(document -> new IndexQueryBuilder()
                    .withId(document.getId())
                    .withObject(document)
                    .build())
            .collect(Collectors.toList());

  }
}
