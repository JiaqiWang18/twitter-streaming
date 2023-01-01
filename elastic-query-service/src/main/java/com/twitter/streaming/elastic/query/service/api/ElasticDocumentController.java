package com.twitter.streaming.elastic.query.service.api;

import com.twitter.streaming.elastic.query.service.business.ElasticQueryService;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.twitter.streaming.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final ElasticQueryService elasticQueryService;

    @Value("${server.port}")
    private String port;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @Operation(summary = "Get all documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all documents", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                        schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    ),
            }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved document", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    ),
            }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        LOG.debug("Elasticsearch returned document with id {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved document", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
                    ),
            }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        LOG.debug("Elasticsearch returned document with id {}", response.getId());
        return ResponseEntity.ok(getV2Model(response));
    }

    @Operation(summary = "Get document by text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved document", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    ),
            }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {
        List<ElasticQueryServiceResponseModel> response =
                elasticQueryService.getDocumentsByText(elasticQueryServiceRequestModel.getText());
        LOG.info("Elasticsearch returned {} of documents on port {}", response.size(), port);
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel response) {
        ElasticQueryServiceResponseModelV2 modelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(response.getId()))
                .userId(response.getUserId())
                .text(response.getText())
                .text2("version 2")
                .build();
        modelV2.add(response.getLinks());
        return modelV2;
    }
}
