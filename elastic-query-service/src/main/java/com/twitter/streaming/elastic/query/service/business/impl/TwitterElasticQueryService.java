package com.twitter.streaming.elastic.query.service.business.impl;

import com.twitter.streaming.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.client.service.ElasticQueryClient;
import com.twitter.streaming.elastic.query.service.business.ElasticQueryService;
import com.twitter.streaming.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    private final ElasticQueryServiceResponseModelAssembler assembler;

    public TwitterElasticQueryService(ElasticQueryClient<TwitterIndexModel> elasticQueryClient, ElasticQueryServiceResponseModelAssembler assembler) {
        this.elasticQueryClient = elasticQueryClient;
        this.assembler = assembler;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        LOG.info("Getting document by id: {}", id);
        return assembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        LOG.info("Getting documents by text: {}", text);
        return assembler.toModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        LOG.info("Getting all documents");
        return assembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
