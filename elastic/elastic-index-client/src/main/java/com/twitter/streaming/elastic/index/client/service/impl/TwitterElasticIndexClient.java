package com.twitter.streaming.elastic.index.client.service.impl;

import com.twitter.streaming.config.ElasticConfigData;
import com.twitter.streaming.elastic.index.client.service.ElasticIndexClient;
import com.twitter.streaming.elastic.index.client.util.ElasticIndexUtil;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "elastic-config.use-repository", havingValue = "false")
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticIndexClient.class);

  private final ElasticConfigData elasticConfigData;

  private final ElasticsearchOperations elasticsearchOperations;

  private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

  public TwitterElasticIndexClient(ElasticConfigData elasticConfigData,
                                   ElasticsearchOperations elasticsearchOperations,
                                   ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil) {
    this.elasticConfigData = elasticConfigData;
    this.elasticsearchOperations = elasticsearchOperations;
    this.elasticIndexUtil = elasticIndexUtil;
  }

  @Override
  public List<String> save(List<TwitterIndexModel> documents) {
    List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
    List<String> documentIds = elasticsearchOperations.bulkIndex(indexQueries, IndexCoordinates.of(elasticConfigData.getIndexName()));
    LOG.info("Documents saved to ElasticSearch: {}", documentIds);
    return documentIds;
  }
}
