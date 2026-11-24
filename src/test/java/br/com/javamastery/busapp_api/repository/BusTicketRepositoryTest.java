package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.*;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BusTicketRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18"));

    @Autowired
    private BusTicketRepository repository;
    @Autowired
    private EntityManager em;
    @Autowired
    private TravelerRepository travelerRepository;
    @Autowired
    private TripRepository tripRepository;

    private Trip trip;
    private Traveler traveler;

    @BeforeEach
    void setUp(){
        State state = new State();
        ReflectionTestUtils.setField(state, "code", 29L);
        ReflectionTestUtils.setField(state, "name", "Bahia");
        ReflectionTestUtils.setField(state, "uf", "BA");
        em.persist(state);

        City origin = new City();
        ReflectionTestUtils.setField(origin, "city", "Salvador");
        ReflectionTestUtils.setField(origin, "ibgeCode", 2927408L);
        ReflectionTestUtils.setField(origin, "state", state);
        em.persist(origin);

        City destination = new City();
        ReflectionTestUtils.setField(destination, "city", "Feira de Santana");
        ReflectionTestUtils.setField(destination, "ibgeCode", 2910800L);
        ReflectionTestUtils.setField(destination, "state", state);
        em.persist(destination);

        BusCompany company = new BusCompany();
        company.setTradingName("Cidade Sol");
        company.setLegalName("Jequié Viações Ltda");
        company.setCnpj("12345678000114");
        company.setTelephone("73988369725");
        company.setPassword("cidadesol123");
        em.persist(company);

        traveler = new Traveler();
        traveler.setName("Julia Roberts");
        traveler.setEmail("julia.roberts@gmail.com");
        traveler.setPassword("julinha123");
        traveler.setCpf("12345678901");
        traveler.setTelephone("71981983522");
        traveler.setBirthDate(LocalDate.of(2003, 6, 19));
        em.persist(traveler);

        trip = new Trip();
        trip.setCode("TRIP000001");
        trip.setOrigin(origin);
        trip.setDestination(destination);
        trip.setBusCompany(company);
        trip.setPrice(BigDecimal.valueOf(39.00));
        trip.setDepartureTime(LocalTime.of(13, 30));
        trip.setDistanceKM(116.0);
        em.persist(trip);
    }

    private BusTicket persistTicket(String code){
        BusTicket ticket = new BusTicket();
        ticket.setCode(code);
        ticket.setTraveler(traveler);
        ticket.setTrip(trip);
        ticket.setPrice(BigDecimal.valueOf(100));
        ticket.setDepartureDate(LocalDate.now().plusDays(3));
        em.persist(ticket);
        return ticket;
    }

    private void cancelTicket(String code){
        Optional<BusTicket> ticket = repository.findByCode(code);
        ticket.get().setCanceled(true);
        em.flush();
    }

    @Test
    @DisplayName("findbycode must return only the ticket with the given code")
    void findByCode_returnsRightTicket(){
        persistTicket("TICKET0001");
        persistTicket("TICKET0002");
        em.flush();
        em.clear();

        Optional<BusTicket> found = repository.findByCode("TICKET0002");


        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("TICKET0002");
        assertThat(Hibernate.isInitialized(found.get().getTrip())).isTrue();
    }

    @Test
    @DisplayName("findbycode must return empty when the given code does not exist")
    void findByNonExistingCode_returnsEmpty(){
        when(repository.findByCode("TICKET0000")).thenReturn(Optional.empty());

        Optional<BusTicket> found = repository.findByCode("TICKET0000");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAllActiveByTravelerId must return only the active tickets")
    void findAllActiveByTravelerId_returnsActiveOnes(){
        persistTicket("TICKET0001");
        BusTicket second = persistTicket("TICKET0002");
        second.setCanceled(true);
        em.flush();
        em.clear();

        List<BusTicket> activeTickets = repository.findAllActiveByTravelerId(traveler.getId());

        assertThat(activeTickets).hasSize(1);
        assertThat(activeTickets.get(0).getCode()).isEqualTo("TICKET0001");
    }

    @Test
    @DisplayName("findAllByTravelerId must return all tickets")
    void findAllByTravelerId_returnsAllTickets(){
        Optional<Traveler> foundTraveler = travelerRepository.findByEmail("julia.roberts@gmail.com");
        when(repository.findAllByTravelerId(foundTraveler.get().getId())).thenReturn(List.of(new BusTicket()));

        List<BusTicket> activeTickets = repository.findAllByTravelerId(foundTraveler.get().getId());

        assertThat(activeTickets).hasSize(2);
    }

    @Test
    @DisplayName("existsByTripId must be true only when a ticket uses the trip")
    void existsByExistingTrip_returnsTrue(){
        assertThat(repository.existsByTripId(trip.getId())).isFalse();
        persistTicket("TICKET0001");
        em.flush();
        assertThat(repository.existsByTripId(trip.getId())).isTrue();
    }

    @Test
    @DisplayName("existsByTripId must return false when there is no ticket related to this trip id")
    void existsByTrip_returnsFalse(){
        when(repository.existsByTripId(0L)).thenReturn(false);

        boolean exists = repository.existsByTripId(0l);

        assertThat(exists).isFalse();
    }
}
