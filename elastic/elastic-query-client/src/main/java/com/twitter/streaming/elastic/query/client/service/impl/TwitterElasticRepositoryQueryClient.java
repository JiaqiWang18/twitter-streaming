package com.twitter.streaming.elastic.query.client.service.impl;

import com.twitter.streaming.common.util.CollectionsUtil;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.client.exception.ElasticQueryClientException;
import com.twitter.streaming.elastic.query.client.repository.TwitterElasticsearchQueryRepository;
import com.twitter.streaming.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final TwitterElasticsearchQueryRepository twitterElasticsearchQueryRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticsearchQueryRepository repository) {
        this.twitterElasticsearchQueryRepository = repository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = twitterElasticsearchQueryRepository.findById(id);
        LOG.info("Document found for id: {}",
                searchResult.orElseThrow(
                        () -> new ElasticQueryClientException("No document found with id: " + id)
                ).getId()
        );
        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = twitterElasticsearchQueryRepository.findByText(text);
        LOG.info("{} of documents with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> searchResult =
                CollectionsUtil.getInstance().getListFromIterable(twitterElasticsearchQueryRepository.findAll());
        LOG.info("{} of documents retrieved successfully", searchResult.size());
        return searchResult;
    }
}
