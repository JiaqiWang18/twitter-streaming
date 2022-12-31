package com.twitter.streaming.elastic.query.service.api;

import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ElasticDocumentController.class);

    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
        List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
        LOG.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponseModel response =
                ElasticQueryServiceResponseModel.builder()
                        .id(id)
                        .build();
        LOG.debug("Elasticsearch returned document with id {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
            @RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {
        List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
                ElasticQueryServiceResponseModel.builder()
                        .text(elasticQueryServiceRequestModel.getText())
                        .build();
        response.add(elasticQueryServiceResponseModel);
        LOG.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }
}
