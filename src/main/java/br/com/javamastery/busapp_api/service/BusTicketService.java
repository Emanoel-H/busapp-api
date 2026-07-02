package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusTicketCanceledResponse;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusTicketService {
    private final BusTicketRepository busTicketRepository;
    private final TravelerRepository travelerRepository;
    private final TripRepository tripRepository;

    public BusTicketResponse buy(BusTicketRequest busTicketRequest){
        Trip trip = findTripOrThrow(busTicketRequest.getTrip_code());
        Traveler traveler = findTravelerOrThrow(busTicketRequest.getTraveler_id());

        if (!trip.isActive())
            throw new HandlerConfig(HttpStatus.BAD_REQUEST, "Trip is no longer active");

        return toResponse(new BusTicket(busTicketRequest, traveler, trip));
    }

    public List<BusTicketResponse> listAllByTraveler(Long traveler_id, boolean includeCanceled){
        List<BusTicket> tickets = includeCanceled
                ? busTicketRepository.findAllByTravelerId(traveler_id)
                : busTicketRepository.findAllActiveByTravelerId(traveler_id);

        if (tickets.isEmpty())
           throw new HandlerConfig(HttpStatus.NOT_FOUND, "Tickets not found for the traveler id: " + traveler_id);

        return tickets.stream().map(this::toResponse).toList();
    }

    public BusTicketResponse findByCode(String code){
        return toResponse(findBusTicketOrThrow(code));
    }

    public BusTicketCanceledResponse cancelTicket(String code){
        BusTicket busTicket = findBusTicketOrThrow(code);

        if (busTicket.isCanceled())
            throw new HandlerConfig(HttpStatus.BAD_REQUEST, "Ticket is already canceled");

        LocalDateTime departureDateTime = LocalDateTime.of(busTicket.getDepartureDate(), busTicket.getTrip().getDepartureTime());

        if (!LocalDateTime.now().isBefore(departureDateTime.minusHours(1)))
            throw new HandlerConfig(HttpStatus.BAD_REQUEST, "Cancellation window has closed. Tickets must be canceled at least 1 hour before departure");

        busTicket.cancelTicket();
        findTravelerOrThrow(busTicket.getTraveler().getId()).addCredits(busTicket.getPrice());

        return toCanceledResponse(busTicketRepository.save(busTicket));
    }

    public Traveler findTravelerOrThrow(Long traveler_id) {
        return travelerRepository.findById(traveler_id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Traveler not found!"));
    }

    public Trip findTripOrThrow(String trip_code) {
        return tripRepository.findByCode(trip_code)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Trip not found!"));
    }

    public BusTicket findBusTicketOrThrow(String code){
        return busTicketRepository.findByCode(code)
                .orElseThrow(()  -> new HandlerConfig(HttpStatus.NOT_FOUND, "BusTicket not found!"));
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

    public BusTicketCanceledResponse toCanceledResponse(BusTicket busTicket) {
        return BusTicketCanceledResponse.builder()
                .code(busTicket.getCode())
                .price(busTicket.getPrice())
                .departureDate(busTicket.getDepartureDate())
                .travelerName(busTicket.getTraveler().getName())
                .travelerCreditsBalance(busTicket.getTraveler().getCreditsBalance())
                .cpf(busTicket.getTraveler().getCpf())
                .originCity(busTicket.getTrip().getOrigin().getCity())
                .originState(busTicket.getTrip().getOrigin().getState().getName())
                .destinationCity(busTicket.getTrip().getDestination().getCity())
                .destinationState(busTicket.getTrip().getDestination().getState().getName())
                .cancelDate(busTicket.getCancelDate())
                .canceled(busTicket.isCanceled())
                .build();
    }
}
