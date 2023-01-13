package com.twitter.streaming.elastic.query.service.business.impl;

import com.twitter.streaming.config.ElasticQueryServiceConfigData;
import com.twitter.streaming.elastic.query.service.QueryType;
import com.twitter.streaming.elastic.query.service.common.exception.ElasticQueryServiceException;
import com.twitter.streaming.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.twitter.streaming.elastic.model.index.impl.TwitterIndexModel;
import com.twitter.streaming.elastic.query.client.service.ElasticQueryClient;
import com.twitter.streaming.elastic.query.service.business.ElasticQueryService;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceWordCountResponseModel;
import com.twitter.streaming.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    private final ElasticQueryServiceResponseModelAssembler assembler;


    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    private final WebClient.Builder webClientBuilder;

    public TwitterElasticQueryService(ElasticQueryClient<TwitterIndexModel> elasticQueryClient, ElasticQueryServiceResponseModelAssembler assembler, ElasticQueryServiceConfigData elasticQueryServiceConfigData, WebClient.Builder webClientBuilder) {
        this.elasticQueryClient = elasticQueryClient;
        this.assembler = assembler;
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        LOG.info("Getting document by id: {}", id);
        return assembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(String text, String accessToken) {
        LOG.info("Getting documents by text: {}", text);
        List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels =
                assembler.toModels(elasticQueryClient.getIndexModelByText(text));
        return ElasticQueryServiceAnalyticsResponseModel.builder()
                .queryResponseModels(elasticQueryServiceResponseModels)
                .wordCount(getWordCount(text, accessToken))
                .build();
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        LOG.info("Getting all documents");
        return assembler.toModels(elasticQueryClient.getAllIndexModels());
    }

    private Long getWordCount(String text, String accessToken) {
        if (QueryType.KAFKA_STATE_STORE.getType().equals(elasticQueryServiceConfigData.getWebClient().getQueryType())) {
            return getFromKafkaStateStore(text, accessToken).getWordCount();
        }
        return 0L;
    }

    private ElasticQueryServiceWordCountResponseModel getFromKafkaStateStore(String text, String accessToken) {
        ElasticQueryServiceConfigData.Query queryFromKafkaStateStore =
                elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
        return retrieveResponseModel(text, accessToken, queryFromKafkaStateStore);
    }

    private ElasticQueryServiceWordCountResponseModel retrieveResponseModel(String text,
                                                                            String accessToken,
                                                                            ElasticQueryServiceConfigData.Query query) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(query.getMethod()))
                .uri(query.getUri(), uriBuilder -> uriBuilder.build(text))
                .headers(h -> h.setBearerAuth(accessToken))
                .accept(MediaType.valueOf(query.getAccept()))
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new
                                ElasticQueryServiceException(clientResponse.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase())))
                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
                .log()
                .block();

    }

}
