package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.dto.TripRequest;
import br.com.javamastery.busapp_api.dto.TripResponse;
import br.com.javamastery.busapp_api.dto.TripUpdateRequest;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.*;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.CityRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {
    @InjectMocks
    private TripService service;
    @Mock
    private TripRepository repository;
    @Mock
    private BusCompanyRepository busCompanyRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private BusTicketRepository busTicketRepository;
    @Mock
    private OsrmClient osrmClient;

    private City rio, niteroi, saoPaulo;
    private BusCompany company;
    private Trip trip;

    @BeforeEach
    void setUp(){
        rio = setCity(3304557L, "Rio de Janeiro", "Rio de Janeiro", "RJ");
        niteroi = setCity(3303302L, "Niteroi", "Rio de Janeiro", "RJ");
        saoPaulo = setCity(3550308L, "São Paulo", "São Paulo", "SP");

        company = new BusCompany();
        company.setId(1L);
        company.setTradingName("Viação Teste");

        trip = new Trip();
        trip.setId(1L);
        trip.setCode("TRIP000001");
        trip.setOrigin(rio);
        trip.setDestination(saoPaulo);
        trip.setBusCompany(company);
        trip.setPrice(BigDecimal.valueOf(100.00));
        trip.setDepartureTime(LocalTime.of(8,0));
        trip.setDistanceKM(430.0);
        trip.setCategory(Category.INTERSTATE);
        trip.setActive(true);
    }

    @Test
    @DisplayName("Should create trip using the distance returned by OSRM")
    void create_success(){
        TripRequest request = new TripRequest();
        request.setOriginCityIbgeCode(3304557L);
        request.setDestinationCityIbgeCode(3303302L);
        request.setBusCompanyId(1L);
        request.setDepartureTime(LocalTime.of(14, 30));
        request.setPrice(BigDecimal.valueOf(25.00));

        when(cityRepository.findByIBGECode(3304557L)).thenReturn(Optional.of(rio));
        when(cityRepository.findByIBGECode(3303302L)).thenReturn(Optional.of(niteroi));
        when(busCompanyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(osrmClient.getRealDistanceKM(rio, niteroi)).thenReturn(23.4);
        when(repository.save(any(Trip.class))).thenAnswer(inv -> {
            Trip saved = inv.getArgument(0);
            saved.setCategory(Category.INTERCITY);
            return saved;
        });

        TripResponse response = service.create(request);

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDistanceKM()).isEqualTo(23.4);
        assertThat(captor.getValue().getBusCompany()).isSameAs(company);
        assertThat(response.getDistanceKM()).isEqualTo(23.4);
        assertThat(response.getBusCompany()).isEqualTo("Viação Teste");
    }

    @Test
    @DisplayName("Should throw CONFLICT and keep trip active when it has tickets")
    void delete_hasTickets_conflict(){
        when(repository.findByCode("TRIP000001")).thenReturn(Optional.of(trip));
        when(busTicketRepository.existsByTripId(1L)).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() ->
                                        service.delete("TRIP000001"), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(trip.isActive()).isTrue();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete trip successfully when it does not exist in a Bus Ticket")
    void delete_success(){
        when(repository.findByCode("TRIP000001")).thenReturn(Optional.of(trip));
        when(busTicketRepository.existsByTripId(1L)).thenReturn(false);

        service.delete("TRIP000001");

        assertThat(trip.isActive()).isFalse();
        verify(repository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when trip is not found")
    void deleteTrip_NotFound(){
        when(repository.findByCode("TRIP000002")).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.delete("TRIP000002"), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verifyNoInteractions(busTicketRepository);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when origin city does not exist")
    void update_originCityNotFound(){
        TripUpdateRequest updateRequest = new TripUpdateRequest();
        updateRequest.setOriginCityIbgeCode(3303000L);

        when(repository.findByCode("TRIP000001")).thenReturn(Optional.of(trip));
        when(cityRepository.findByIBGECode(3303000L)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() ->
                service.update("TRIP000001", updateRequest), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("City not found");
        verifyNoInteractions(osrmClient);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw not found when BusCompany does not exist")
    void create_busCompanyNotFound(){
        TripRequest request = new TripRequest();
        request.setOriginCityIbgeCode(3304557L);
        request.setDestinationCityIbgeCode(3303302L);
        request.setBusCompanyId(0L);

        when(cityRepository.findByIBGECode(3304557L)).thenReturn(Optional.of(rio));
        when(cityRepository.findByIBGECode(3303302L)).thenReturn(Optional.of(niteroi));
        when(busCompanyRepository.findById(0L)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.create(request), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("Company not found");
        verifyNoInteractions(osrmClient);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Trip successfully when id exists")
    void update_success(){
        TripUpdateRequest updateRequest = new TripUpdateRequest();
        updateRequest.setOriginCityIbgeCode(3304557L);
        updateRequest.setDestinationCityIbgeCode(3300100L);
        updateRequest.setDepartureTime(LocalTime.of(13, 0));
        updateRequest.setPrice(BigDecimal.valueOf(70.00));

        City angra = setCity(3300100L, "Angra dos Reis", "Rio de Janeiro", "RJ");

        when(repository.findByCode("TRIP000001")).thenReturn(Optional.of(trip));
        when(cityRepository.findByIBGECode(3304557L)).thenReturn(Optional.of(rio));
        when(cityRepository.findByIBGECode(3300100L)).thenReturn(Optional.of(angra));
        when(osrmClient.getRealDistanceKM(rio, angra)).thenReturn(150.0);
        when(repository.save(any(Trip.class))).thenReturn(trip);

        var response = service.update("TRIP000001", updateRequest);

        assertNotNull(response);
        assertThat(trip.getOrigin()).isSameAs(rio);
        assertThat(trip.getDestination()).isSameAs(angra);
        assertThat(trip.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(70.00));
        assertThat(trip.getDepartureTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(trip.getDistanceKM()).isEqualTo(150.0);
        assertThat(response.getDistanceKM()).isEqualTo(150.0);
        verify(repository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when there are no active trips")
    void listAll_empty(){
        when(repository.findAllActive()).thenReturn(List.of());

        HandlerConfig ex = catchThrowableOfType(() ->
                service.listAll(), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("no active trips");
    }

    @Test
    @DisplayName("Should return one TripResponse when there is one trip active")
    void listAll_oneTrip(){
        when(repository.findAllActive()).thenReturn(List.of(trip));

        List<TripResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        TripResponse response = result.get(0);
        assertThat(response.getCode()).isEqualTo("TRIP000001");
        assertThat(response.getOriginCity()).isEqualTo(rio.getCity());
        assertThat(response.getDestinationCity()).isEqualTo(saoPaulo.getCity());
        assertThat(response.getBusCompany()).isEqualTo(company.getTradingName());
        assertThat(response.getCategory()).isEqualTo("INTERSTATE");
    }

    @Test
    @DisplayName("Should return NOT_FOUND when no trips exist for this route")
    void listByRoute_empty(){
        when(repository.findAllByOriginAndDestination(3304557L, 3550308L)).thenReturn(List.of());

        HandlerConfig ex = catchThrowableOfType(() ->
                service.listByRoute(3304557L, 3550308L), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("No trips found for this route");
    }

    @Test
    @DisplayName("Should return NOT_FOUND when no trips exist for this BusCompany")
    void listByCompany_empty(){
        when(repository.findAllByBusCompanyId(0L)).thenReturn(List.of());

        HandlerConfig ex = catchThrowableOfType(() ->
                service.listByCompany(0L), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("No trips found for the company");
    }

    @Test
    @DisplayName("Should suggest price as 0.35 per KM using the OSRM distance")
    void suggestPrice_success(){
        when(cityRepository.findByIBGECode(3304557L)).thenReturn(Optional.of(rio));
        when(cityRepository.findByIBGECode(3303302L)).thenReturn(Optional.of(niteroi));
        when(osrmClient.getRealDistanceKM(rio, niteroi)).thenReturn(100.0);

        double price = service.suggestPrice(3304557L, 3303302L);

        assertThat(price).isCloseTo(35.0, within(0.001));
    }

    @Test
    @DisplayName("Should throw NOT_FOUND and skip OSRM when a city does not exist")
    void suggestPrice_cityNotFound(){
        when(cityRepository.findByIBGECode(3304557L)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() ->
                service.suggestPrice(3304557L, 3303302L), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verifyNoInteractions(osrmClient);
    }

    private City setCity (Long ibge_code, String name, String stateName, String uf){
        State state = new  State();
        ReflectionTestUtils.setField(state, "name", stateName);
        ReflectionTestUtils.setField(state, "uf", uf);

        City city = new City();
        ReflectionTestUtils.setField(city, "city", name);
        ReflectionTestUtils.setField(city, "ibgeCode", ibge_code);
        ReflectionTestUtils.setField(city, "state", state);

        return city;
    }
}
