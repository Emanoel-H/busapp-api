package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.model.*;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.CityRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;

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

    private City setCity (Long ibge_code, String name, String stateName, String uf){
        State state = new  State();
        ReflectionTestUtils.setField(state, "name", stateName);
        ReflectionTestUtils.setField(state, "uf", uf);

        City city = new City();
        ReflectionTestUtils.setField(city, "name", name);
        ReflectionTestUtils.setField(city, "ibgeCode", ibge_code);
        ReflectionTestUtils.setField(city, "state", state);

        return city;
    }
}
