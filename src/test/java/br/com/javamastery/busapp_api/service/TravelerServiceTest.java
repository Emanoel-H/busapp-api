package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("Should throw CONFLICT when email already exists")
    void emailAlreadyExits_Conflict(){
        when(repository.existsByEmail(traveler.getEmail())).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() -> repository.existsByEmail(traveler.getEmail()), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should throw CONFLICT when CPF already exists")
    void cpfAlreadyExits_Conflict(){
        when(repository.existsByCpf(traveler.getCpf())).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() -> repository.existsByCpf(traveler.getCpf()), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should register traveler successfully when cpf and email are valid")
    void register_success(){
        when(repository.existsByCpf(traveler.getCpf())).thenReturn(false);
        when(repository.existsByEmail(traveler.getEmail())).thenReturn(false);

        traveler.setPassword(passwordEncoder.encode("traveler123456"));

        when(repository.save(traveler)).thenReturn(traveler);

        verify(repository, times(1)).save(any(Traveler.class));
    }
}
