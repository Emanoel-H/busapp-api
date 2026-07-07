package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.dto.TripRequest;
import br.com.javamastery.busapp_api.dto.TripResponse;
import br.com.javamastery.busapp_api.dto.TripUpdateRequest;
import br.com.javamastery.busapp_api.dto.TripUpdateResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.model.City;
import br.com.javamastery.busapp_api.model.Trip;
import br.com.javamastery.busapp_api.model.policy.TripPolicy;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.CityRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final BusCompanyRepository busCompanyRepository;
    private final CityRepository cityRepository;
    private final BusTicketRepository busTicketRepository;
    private final OsrmClient osrmClient;
    private final TripPolicy policy;

    public TripResponse create(TripRequest tripRequest) {
        City origin = findCityOrThrow(tripRequest.getOriginCityIbgeCode());
        City destination = findCityOrThrow(tripRequest.getDestinationCityIbgeCode());
        BusCompany busCompany = findBusCompanyOrThrow(tripRequest.getBusCompanyId());
        double distanceKm = policy.calculateDistance(origin, destination, osrmClient);
        return toResponse(tripRepository.save(new Trip(tripRequest, origin, destination, busCompany,  distanceKm)));
    }


    public List<TripResponse> listByRoute(Long originCode, Long destinationCode) {
        List<Trip> trips = tripRepository.findAllByOriginAndDestination(originCode, destinationCode);

        if (trips.isEmpty())
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "No trips found for this route");

        return trips.stream().map(this::toResponse).toList();
    }

    public List<TripResponse> listAll(){
        List<Trip> trips = tripRepository.findAllActive();

        if (trips.isEmpty())
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "There are no active trips");

        return trips.stream().map(this::toResponse).toList();
    }

    public List<TripResponse> listByCompany(Long companyId) {
        List<Trip> trips = tripRepository.findAllByBusCompanyId(companyId);

        if (trips.isEmpty())
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "No trips found for the company with id: " + companyId);

        return trips.stream().map(this::toResponse).toList();
    }

    public TripResponse findByCode(String code) {
        return toResponse(findTripOrThrow(code));
    }

    public TripUpdateResponse update(String code, TripUpdateRequest tripUpdateRequest){
        Trip trip  = findTripOrThrow(code);
        City origin = findCityOrThrow(tripUpdateRequest.getOriginCityIbgeCode());
        City destination = findCityOrThrow(tripUpdateRequest.getDestinationCityIbgeCode());
        double distanceKm = osrmClient.getRealDistanceKM(origin, destination);
        trip.tripUpdateRequest(tripUpdateRequest, origin, destination, distanceKm);

        return toUpdateResponse(tripRepository.save(trip));
    }

    public void delete(String code){
        Trip trip = findTripOrThrow(code);

        if (busTicketRepository.existsByTripId(trip.getId()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Cannot deactivate trip: " + code + ": it has tickets associated.");

        trip.deactivate();
        tripRepository.save(trip);
    }

    public double suggestPrice(Long originCode, Long destinationCode){
        City origin = findCityOrThrow(originCode);
        City destination = findCityOrThrow(destinationCode);
        return new OsrmClient(new ObjectMapper()).getRealDistanceKM(origin, destination) * 0.35;
    }

    public City findCityOrThrow (Long ibgeCode) {
        return cityRepository.findByIBGECode(ibgeCode)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "City not found for the code: " + ibgeCode));
    }

    public BusCompany findBusCompanyOrThrow (Long id) {
        return busCompanyRepository.findById(id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Company not found for the id: " + id));
    }

    public Trip findTripOrThrow(String code){
        return tripRepository.findByCode(code)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "Trip not found for the code: " + code));
    }

    public TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .code(trip.getCode())
                .originCity(trip.getOrigin().getCity())
                .originState(trip.getOrigin().getState().getName())
                .destinationCity(trip.getDestination().getCity())
                .destinationState(trip.getDestination().getState().getName())
                .busCompany(trip.getBusCompany().getTradingName())
                .price(trip.getPrice())
                .departureTime(trip.getDepartureTime())
                .distanceKM(trip.getDistanceKM())
                .category(trip.getCategory().toString())
                .build();
    }

    public TripUpdateResponse toUpdateResponse(Trip trip) {
        return TripUpdateResponse.builder()
                .id(trip.getId())
                .code(trip.getCode())
                .originCity(trip.getOrigin().getCity())
                .originState(trip.getOrigin().getState().getName())
                .destinationCity(trip.getDestination().getCity())
                .destinationState(trip.getDestination().getState().getName())
                .price(trip.getPrice())
                .departureTime(trip.getDepartureTime())
                .distanceKM(trip.getDistanceKM())
                .category(trip.getCategory().toString())
                .build();
    }
}
