package com.twitter.streaming.elastic.query.client.service.impl;

import com.twitter.streaming.config.ElasticConfigData;
import com.twitter.streaming.config.ElasticQueryConfigData;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.client.exception.ElasticQueryClientException;
import com.twitter.streaming.elastic.query.client.service.ElasticQueryClient;
import com.twitter.streaming.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final ElasticConfigData elasticConfigData;

    private final ElasticQueryConfigData elasticQueryConfigData;

    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;


    public TwitterElasticQueryClient(ElasticConfigData elasticConfigData, ElasticQueryConfigData elasticQueryConfigData, ElasticsearchOperations elasticsearchOperations, ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil) {
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticQueryUtil = elasticQueryUtil;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> hit = elasticsearchOperations.searchOne(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        if (hit == null) {
            LOG.error("No document found for id: {}", id);
            throw new ElasticQueryClientException("No document found with id: " + id);
        }
        LOG.info("Document found for id: {}", id);
        return hit.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        return search(query,"{} of documents with text {} retrieved successfully", text);
    }


    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        return search(query, "{} of documents retrieved successfully");
    }

    private List<TwitterIndexModel> search(Query query, String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> hits = elasticsearchOperations.search(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        LOG.info(logMessage,hits.getTotalHits(), logParams);
        return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
