package com.twitter.streaming.elastic.query.service.tansformer;

import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticToResponseModelTransformer {
    public ElasticQueryServiceResponseModel getResponseModel(TwitterIndexModel indexModel) {
        return ElasticQueryServiceResponseModel.builder()
                .id(indexModel.getId())
                .userId(indexModel.getUserId())
                .text(indexModel.getText())
                .createdAt(indexModel.getCreatedAt())
                .build();
    }

    public List<ElasticQueryServiceResponseModel> getResponseModels(List<TwitterIndexModel> indexModelList) {
        return indexModelList.stream()
                .map(this::getResponseModel)
                .collect(Collectors.toList());
    }
}
