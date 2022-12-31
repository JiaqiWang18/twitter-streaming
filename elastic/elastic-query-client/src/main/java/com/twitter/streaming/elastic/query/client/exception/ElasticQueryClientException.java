package com.twitter.streaming.elastic.query.client.exception;

public class ElasticQueryClientException extends RuntimeException {
    public ElasticQueryClientException(String message) {
        super(message);
    }

    public ElasticQueryClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticQueryClientException() {
    }
}
