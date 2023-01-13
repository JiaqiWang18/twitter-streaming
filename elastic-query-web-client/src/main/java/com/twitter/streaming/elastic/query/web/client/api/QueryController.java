package com.twitter.streaming.elastic.query.web.client.api;

import com.twitter.streaming.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.twitter.streaming.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.twitter.streaming.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.twitter.streaming.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class QueryController {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(QueryController.class);

    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient elasticQueryWebClient) {
        this.elasticQueryWebClient = elasticQueryWebClient;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel,
                              Model model) {
        LOG.info("Query by text: {}", requestModel.getText());
        ElasticQueryWebClientAnalyticsResponseModel responseModel = elasticQueryWebClient
                .getDataByText(requestModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModel.getQueryResponseModels());
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("wordCount", responseModel.getWordCount());
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }
}
