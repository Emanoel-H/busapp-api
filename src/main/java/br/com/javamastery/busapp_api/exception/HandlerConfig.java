package br.com.javamastery.busapp_api.exception;

public class HandlerConfig extends RuntimeException {
    public HandlerConfig(String message) {
        super(message);
    }
}
