package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.TravelerRequest;
import br.com.javamastery.busapp_api.dto.TravelerResponse;
import br.com.javamastery.busapp_api.service.TravelerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travelers")
@RequiredArgsConstructor
public class TravelerController {
    private final TravelerService service;

    @PostMapping
    public ResponseEntity<TravelerResponse> register(@RequestBody @Valid TravelerRequest travelerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(travelerRequest));
    }

}
