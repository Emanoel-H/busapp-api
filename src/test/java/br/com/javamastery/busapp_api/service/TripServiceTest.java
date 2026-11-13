package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.model.City;
import br.com.javamastery.busapp_api.model.Trip;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.CityRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    
}
