package com.twitter.streaming.elastic.index.client.service.impl;

import com.twitter.streaming.elastic.index.client.repository.TwitterElasticsearchIndexRepository;
import com.twitter.streaming.elastic.index.client.service.ElasticIndexClient;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "elastic-config.use-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticIndexClient.class);

  private final TwitterElasticsearchIndexRepository twitterElasticsearchIndexRepository;

  public TwitterElasticRepositoryIndexClient(TwitterElasticsearchIndexRepository twitterElasticsearchIndexRepository) {
    this.twitterElasticsearchIndexRepository = twitterElasticsearchIndexRepository;
  }

  @Override
  public List<String> save(List<TwitterIndexModel> documents) {
    List<TwitterIndexModel> repositoryResponse = (List<TwitterIndexModel>) twitterElasticsearchIndexRepository.saveAll(documents);
    List<String> ids = repositoryResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
    LOG.info("Documents saved to ElasticSearch: {}", ids);
    return ids;
  }
}
