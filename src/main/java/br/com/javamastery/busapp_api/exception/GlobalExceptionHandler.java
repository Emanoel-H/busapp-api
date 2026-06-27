package br.com.javamastery.busapp_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerConfig.class)
    public ResponseEntity<Map<String, Object>> customHandlerException(HandlerConfig ex){
        return buildResponse(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation Failed");

        return buildResponse(new HandlerConfig(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex){
        return buildResponse(new HandlerConfig(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    private ResponseEntity<Map<String,Object>> buildResponse(HandlerConfig ex){
        Map<String,Object> map = new HashMap<>();
        map.put("TimeStamp", LocalDateTime.now().toString());
        map.put("Status",ex.getStatus().value());
        map.put("Message",ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(map);
    }
}
