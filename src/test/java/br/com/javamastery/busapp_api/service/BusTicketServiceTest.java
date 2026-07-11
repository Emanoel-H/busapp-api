package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.model.BusTicket;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.model.Trip;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BusTicketServiceTest {
    @InjectMocks
    private BusTicketService busTicketService;
    @Mock
    private BusTicketRepository busTicketRepository;
    @Mock
    private TravelerRepository travelerRepository;
    @Mock
    private TripRepository tripRepository;
    private BusTicket busTicket;
    private Traveler traveler;
    private Trip trip;


}
