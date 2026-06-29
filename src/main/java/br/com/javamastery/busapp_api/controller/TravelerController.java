package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.*;
import br.com.javamastery.busapp_api.service.TravelerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/travelers")
@RequiredArgsConstructor
public class TravelerController {
    private final TravelerService service;

    @PostMapping
    public ResponseEntity<TravelerResponse> register(@RequestBody @Valid TravelerRequest travelerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(travelerRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelerUpdateResponse> updateById(@PathVariable Long id, @RequestBody TravelerUpdateRequest travelerUpdateRequest) {
        return ResponseEntity.ok(service.updateById(id, travelerUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/credits")
    public ResponseEntity<TravelerCreditsResponse> addCredits(@PathVariable Long id, @RequestParam BigDecimal credits) {
        return ResponseEntity.ok(service.addCredits(id, credits));
    }
}
