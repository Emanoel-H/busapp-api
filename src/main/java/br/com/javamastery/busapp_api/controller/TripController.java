package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.TripRequest;
import br.com.javamastery.busapp_api.dto.TripResponse;
import br.com.javamastery.busapp_api.dto.TripUpdateRequest;
import br.com.javamastery.busapp_api.dto.TripUpdateResponse;
import br.com.javamastery.busapp_api.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> create(@RequestBody TripRequest tripRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(tripRequest));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> listAll(@RequestParam(required = false) Long originCode, @RequestParam(required = false) Long destinationCode, @RequestParam(required = false) Long companyId) {
        if (originCode != null && destinationCode != null)
            return ResponseEntity.ok(tripService.listByRoute(originCode, destinationCode));

        if (companyId != null)
            return ResponseEntity.ok(tripService.listByCompany(companyId));

        return ResponseEntity.ok(tripService.listAll());
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<TripResponse> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(tripService.findByCode(code));
    }

    @PutMapping("/{code}")
    public ResponseEntity<TripUpdateResponse> update(@PathVariable String code, @RequestBody TripUpdateRequest tripUpdateRequest) {
        return ResponseEntity.ok(tripService.update(code, tripUpdateRequest));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        tripService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
