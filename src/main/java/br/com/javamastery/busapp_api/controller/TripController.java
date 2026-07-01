package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.TripRequest;
import br.com.javamastery.busapp_api.dto.TripResponse;
import br.com.javamastery.busapp_api.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> create(@RequestBody TripRequest tripRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(tripRequest));
    }
}
