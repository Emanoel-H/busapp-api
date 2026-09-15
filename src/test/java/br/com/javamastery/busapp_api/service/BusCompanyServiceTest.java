package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BusCompanyServiceTest {
    @InjectMocks
    private BusCompanyService service;
    @Mock
    private BusCompanyRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private BusCompany busCompany;
    private BusCompanyRequest validRequest;

    @BeforeEach
    void setUp(){
        validRequest = new BusCompanyRequest();
        validRequest.setLegalName("Itapemirim Ltda");
        validRequest.setTradingName("Viação Itapemirim");
        validRequest.setCnpj("12345678000195");
        validRequest.setEmail("itapemirim.trips@gmail.com");
        validRequest.setPassword(passwordEncoder.encode("ItapemirimCompany123"));
        validRequest.setTelephone("71983569425");

        busCompany = new BusCompany(validRequest, validRequest.getPassword());

        busCompany.setCreatedAt(LocalDateTime.now());
        busCompany.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should throw CONFLICT when email already exists")
    void emailAlreadyExits_Conflict(){
        when(repository.existsByEmail(busCompany.getEmail())).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() -> repository.existsByEmail(busCompany.getEmail()), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should throw CONFLICT when CNPJ already exists")
    void cnpjAlreadyExits_Conflict(){
        when(repository.existsByCnpj(busCompany.getCnpj())).thenReturn(true);

        HandlerConfig ex = catchThrowableOfType(() -> repository.existsByCnpj(busCompany.getCnpj()), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
    

}
