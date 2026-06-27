package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyResponse;
import br.com.javamastery.busapp_api.service.BusCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class BusCompanyController {
    private final BusCompanyService service;

    @PostMapping
    public ResponseEntity<BusCompanyResponse> register(@RequestBody @Valid BusCompanyRequest busCompanyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(busCompanyRequest));
    }
}
