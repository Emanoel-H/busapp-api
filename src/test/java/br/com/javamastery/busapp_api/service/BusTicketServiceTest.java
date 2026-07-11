package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusTicket;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.model.Trip;
import br.com.javamastery.busapp_api.repository.BusTicketRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BusTicketServiceTest {
    @InjectMocks
    private BusTicketService service;
    @Mock
    private BusTicketRepository busTicketRepository;
    @Mock
    private TravelerRepository travelerRepository;
    @Mock
    private TripRepository tripRepository;
    private BusTicket busTicket;
    private Traveler traveler;
    private Trip trip;

    @Test
    @DisplayName("Should throw NOT_FOUND when ticket code does not exist")
    void cancelTicket_NotFound(){
        when(busTicketRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.cancelTicket("INVALID"), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw BAD_REQUEST when ticket is already canceled")
    void ticketAlreadyCanceled_BadRequest(){
        busTicket.setCanceled(true);
        when(busTicketRepository.findByCode("TICKET0001")).thenReturn(Optional.of(busTicket));

        HandlerConfig ex = catchThrowableOfType(() -> service.cancelTicket("TICKET0001"), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("already canceled");
    }

}
