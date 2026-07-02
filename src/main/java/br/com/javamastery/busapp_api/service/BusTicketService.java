package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusTicketRequest;
import br.com.javamastery.busapp_api.dto.BusTicketResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusTicket;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.model.Trip;
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

    public Trip findTripOrThrow(String trip_code) {
        return tripRepository.findByCode(trip_code)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Trip not found!"));
    }

    public BusTicketResponse toResponse(BusTicket busTicket) {
        return BusTicketResponse.builder()
                .code(busTicket.getCode())
                .price(busTicket.getPrice())
                .departureDate(busTicket.getDepartureDate())
                .travelerName(busTicket.getTraveler().getName())
                .cpf(busTicket.getTraveler().getCpf())
                .originCity(busTicket.getTrip().getOrigin().getCity())
                .originState(busTicket.getTrip().getOrigin().getState().getName())
                .destinationCity(busTicket.getTrip().getDestination().getCity())
                .destinationState(busTicket.getTrip().getDestination().getState().getName())
                .saleDate(busTicket.getSaleDate())
                .createdAt(busTicket.getCreatedAt())
                .build();
    }
}
