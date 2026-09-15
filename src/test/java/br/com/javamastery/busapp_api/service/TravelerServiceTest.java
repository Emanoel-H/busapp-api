package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.TravelerRequest;
import br.com.javamastery.busapp_api.dto.TravelerResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private TravelerRequest validRequest;

    @BeforeEach
    void setUp(){
        validRequest = new TravelerRequest();
        validRequest.setName("Traveler");
        validRequest.setCpf("12345678902");
        validRequest.setEmail("traveler@gmail.com");
        validRequest.setPassword(passwordEncoder.encode("traveler123"));

        traveler = new Traveler(validRequest, validRequest.getPassword());
        traveler.setId(1L);
        traveler.setCreditsBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should throw CONFLICT when email already exists")
    void emailAlreadyExits_Conflict(){
        when(repository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() ->
                service.register(validRequest), HandlerConfig.class);

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

    @Test
    @DisplayName("Should throw NOT_FOUND when traveler id does not exist")
    void findTraveler_NotFound(){
        when(repository.findById(0L)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.findOrThrow(0L), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should find traveler successfully when traveler id exists")
    void findTraveler_success(){
        when(repository.findById(1L)).thenReturn(Optional.of(traveler));

        TravelerResponse response = service.toResponse(traveler);

        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should delete traveler successfully when traveler id exists")
    void deleteTraveler_success(){
        when(repository.findById(1L)).thenReturn(Optional.of(traveler));

        service.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when traveler id does not exist")
    void deleteTraveler_NotFound(){
        when(repository.findById(0L)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.deleteById(0L), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw BAD_REQUEST when credits amount is not greater than zero")
    void addCredits_BadRequest(){
        HandlerConfig ex = catchThrowableOfType(() -> service.addCredits(0L, BigDecimal.ZERO),
                                                                                HandlerConfig.class);
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should add credits successfully when credits amount is greater than zero")
    void addCredits_success(){
        when(repository.findById(1L)).thenReturn(Optional.of(traveler));
        when(repository.save(traveler)).thenReturn(traveler);

        service.addCredits(1L, BigDecimal.valueOf(50.00));

        assertThat(traveler.getCreditsBalance()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }
}
