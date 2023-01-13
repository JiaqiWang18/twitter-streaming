package com.twitter.streaming.elastic.query.service.business;

import com.twitter.streaming.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;

import java.util.List;


public interface ElasticQueryService {
    ElasticQueryServiceResponseModel getDocumentById(String id);

    ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(String text, String accessToken);

    List<ElasticQueryServiceResponseModel> getAllDocuments();
}
