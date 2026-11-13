package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.model.City;
import br.com.javamastery.busapp_api.model.State;
import br.com.javamastery.busapp_api.model.Trip;
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
