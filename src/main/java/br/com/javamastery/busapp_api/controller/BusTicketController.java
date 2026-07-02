package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.BusTicketCanceledResponse;
import br.com.javamastery.busapp_api.dto.BusTicketRequest;
import br.com.javamastery.busapp_api.dto.BusTicketResponse;
import br.com.javamastery.busapp_api.service.BusTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class BusTicketController {
    private final BusTicketService busTicketService;

    @PostMapping
    public ResponseEntity<BusTicketResponse> buy(BusTicketRequest busTicketRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busTicketService.buy(busTicketRequest));
    }

    @GetMapping("/{code}")
    public ResponseEntity<BusTicketResponse> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(busTicketService.findByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<BusTicketResponse>> listAllByTraveler(@RequestParam Long traveler_id, @RequestParam(required = false) boolean includeCanceled) {
        return ResponseEntity.ok(busTicketService.listAllByTraveler(traveler_id, includeCanceled));
    }

    @DeleteMapping("/{code}/cancel")
    public ResponseEntity<BusTicketCanceledResponse> cancelTicket(@PathVariable String code) {
        return ResponseEntity.ok(busTicketService.cancelTicket(code));
    }
}
