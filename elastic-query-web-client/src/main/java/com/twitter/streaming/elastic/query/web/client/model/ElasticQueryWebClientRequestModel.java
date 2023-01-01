package com.twitter.streaming.elastic.query.web.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElasticQueryWebClientRequestModel {
    private String id;
    @NotEmpty
    private String text;
}
