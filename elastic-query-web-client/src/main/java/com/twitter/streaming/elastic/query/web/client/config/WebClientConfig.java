package com.twitter.streaming.elastic.query.web.client.config;

import com.twitter.streaming.config.ElasticQueryWebClientConfigData;
import com.twitter.streaming.config.UserConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
@LoadBalancerClient(name = "elastic-query-service", configuration = ElasticQueryServiceInstanceListSupplierConfig.class)
public class WebClientConfig {
    private final ElasticQueryWebClientConfigData.WebClient webClientConfigData;

    private final UserConfigData userConfigData;

    public WebClientConfig(ElasticQueryWebClientConfigData webClientConfigData, UserConfigData userConfigData) {
        this.webClientConfigData = webClientConfigData.getWebClient();
        this.userConfigData = userConfigData;
    }

    @LoadBalanced
    @Bean(name = "webClientBuilder")
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(ExchangeFilterFunctions
                        .basicAuthentication(userConfigData.getUsername(), userConfigData.getPassword()))
                .baseUrl(webClientConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, webClientConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, webClientConfigData.getAcceptType())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(webClientConfigData.getMaxInMemorySize()));
    }

    private TcpClient getTcpClient() {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConfigData.getConnectionTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(webClientConfigData.getReadTimeoutMs()));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(webClientConfigData.getWriteTimeoutMs()));
                });
    }
}