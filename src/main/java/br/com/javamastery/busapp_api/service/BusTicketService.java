package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusTicketRequest;
import br.com.javamastery.busapp_api.dto.BusTicketResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusTicketService {
    private final BusTicketRepository busTicketRepository;
    private final TravelerRepository travelerRepository;
    private final TripRepository tripRepository;

    public Traveler findTravelerOrThrow(Long traveler_id) {
        return travelerRepository.findById(traveler_id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Traveler not found!"));
    }
}
