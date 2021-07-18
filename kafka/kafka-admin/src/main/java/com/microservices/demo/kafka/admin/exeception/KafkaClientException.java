package com.microservices.demo.kafka.admin.exeception;

public class KafkaClientException extends RuntimeException {

    public KafkaClientException() {}

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
