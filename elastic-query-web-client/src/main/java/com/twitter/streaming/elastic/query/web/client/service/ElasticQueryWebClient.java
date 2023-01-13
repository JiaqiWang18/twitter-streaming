package com.twitter.streaming.elastic.query.web.client.service;


import com.twitter.streaming.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.twitter.streaming.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;

import java.util.List;

public interface ElasticQueryWebClient {
    ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel);
}
