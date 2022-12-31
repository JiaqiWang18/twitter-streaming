package com.twitter.streaming.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-config")
public class ElasticQueryConfigData {
    private String textField;
}
