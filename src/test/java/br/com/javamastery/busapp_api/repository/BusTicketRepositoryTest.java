package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
        company.setCnpj("12345678000114");
        em.persist(company);

        traveler = new Traveler();
        traveler.setName("Julia Roberts");
        traveler.setEmail("julia.roberts@gmail.com");
        traveler.setPassword("julinha123");
        traveler.setCpf("12345678901");
        traveler.setBirthDate(LocalDate.of(2003, 06, 19));
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
}
