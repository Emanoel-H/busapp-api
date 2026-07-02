package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.BusTicketRequest;
import br.com.javamastery.busapp_api.dto.BusTicketResponse;
import br.com.javamastery.busapp_api.service.BusTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class BusTicketController {
    private final BusTicketService busTicketService;

    @PostMapping
    public ResponseEntity<BusTicketResponse> buy(BusTicketRequest busTicketRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busTicketService.buy(busTicketRequest));
    }
}
