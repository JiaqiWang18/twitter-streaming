package com.twitter.streaming.elastic.config;

import com.twitter.streaming.config.ElasticConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.twitter.streaming.elastic")
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

  private final ElasticConfigData elasticConfigData;

  public ElasticsearchConfig(ElasticConfigData elasticConfigData) {
    this.elasticConfigData = elasticConfigData;
  }

  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {
    UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl()).build();
    return new RestHighLevelClient(
            RestClient.builder(new HttpHost(
                    Objects.requireNonNull(serverUri.getHost()),
                    serverUri.getPort(),
                    serverUri.getScheme())
            ).setRequestConfigCallback(
                    requestConfigBuilder ->
                            requestConfigBuilder
                                    .setConnectTimeout(elasticConfigData.getConnectionTimeoutMs())
                                    .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())
            )
    );
  }

  @Bean
  public ElasticsearchOperations elasticSearchTemplate() {
    return new ElasticsearchRestTemplate(elasticsearchClient());
  }
}
