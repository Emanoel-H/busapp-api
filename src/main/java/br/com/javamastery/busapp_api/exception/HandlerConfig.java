package br.com.javamastery.busapp_api.exception;

import org.springframework.http.HttpStatus;

public class HandlerConfig extends RuntimeException {
    private final HttpStatus status;


    public HandlerConfig(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
