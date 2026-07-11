package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class TravelerServiceTest {
    @InjectMocks
    private TravelerService service;
    @Mock
    private TravelerRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Traveler traveler;

    @BeforeEach
    void setUp(){
        traveler = new Traveler();
        traveler.setId(1L);
        traveler.setName("Traveler");
        traveler.setCpf("12345678902");
        traveler.setCreditsBalance(BigDecimal.valueOf(100.00));
        traveler.setEmail("traveler@gmail.com");
        traveler.setPassword(passwordEncoder.encode("traveler123"));
    }
}
