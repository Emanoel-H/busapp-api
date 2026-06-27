package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyResponse;
import br.com.javamastery.busapp_api.dto.BusCompanyUpdateRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyUpdateResponse;
import br.com.javamastery.busapp_api.service.BusCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class BusCompanyController {
    private final BusCompanyService service;

    @PostMapping
    public ResponseEntity<BusCompanyResponse> register(@RequestBody @Valid BusCompanyRequest busCompanyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(busCompanyRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusCompanyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<BusCompanyUpdateResponse> updateById(@PathVariable Long id, @RequestBody BusCompanyUpdateRequest busCompanyUpdateRequest) {
        return ResponseEntity.ok(service.update(id, busCompanyUpdateRequest));
    }
}
