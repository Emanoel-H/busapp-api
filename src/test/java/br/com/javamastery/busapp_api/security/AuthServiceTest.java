package br.com.javamastery.busapp_api.security;

import br.com.javamastery.busapp_api.dto.LoginRequest;
import br.com.javamastery.busapp_api.dto.LoginResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService service;
    @Mock
    private BusCompanyRepository companyRepository;
    @Mock
    private TravelerRepository travelerRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should login a company and never look for a traveler")
    void login_company_success(){
        BusCompany company = new BusCompany();
        company.setEmail("empresa@teste.com");
        company.setPassword("hash");

        LoginRequest request = new LoginRequest();
        request.setEmail("empresa@teste.com");
        request.setPassword("teste123");

        when(companyRepository.findByEmail("empresa@teste.com")).thenReturn(Optional.of(company));
        when(passwordEncoder.matches("teste123", "hash")).thenReturn(true);
        when(jwtService.generateToken("empresa@teste.com", "COMPANY")).thenReturn("token-company");

        LoginResponse response = service.login(request);

        assertThat(response.getRole()).isEqualTo("COMPANY");
        assertThat(response.getToken()).isEqualTo("token-company");
        assertThat(response.getEmail()).isEqualTo("empresa@teste.com");
        verifyNoInteractions(travelerRepository);
    }

    @Test
    @DisplayName("Should login a traveler when a busCompany is not found for this email")
    void login_traveler_success(){
        Traveler traveler = new Traveler();
        traveler.setEmail("viajante@teste.com");
        traveler.setPassword("hash-traveler");

        LoginRequest request = new LoginRequest();
        request.setEmail("viajante@teste.com");
        request.setPassword("senha123");

        when(companyRepository.findByEmail("viajante@teste.com")).thenReturn(Optional.empty());
        when(travelerRepository.findByEmail("viajante@teste.com")).thenReturn(Optional.of(traveler));
        when(passwordEncoder.matches("senha123", "hash-traveler")).thenReturn(true);
        when(jwtService.generateToken("viajante@teste.com", "TRAVELER")).thenReturn("token-traveler");

        LoginResponse response = service.login(request);

        assertThat(response.getRole()).isEqualTo("TRAVELER");
        assertThat(response.getToken()).isEqualTo("token-traveler");
        assertThat(response.getEmail()).isEqualTo("viajante@teste.com");
    }

    @Test
    @DisplayName("Should throw UNAUTHORIZED when there is no traveler or busCompany for this email")
    void login_unauthorizedEmail(){
        String email = "wrong@email.com";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("wrongPassword123");

        when(companyRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(travelerRepository.findByEmail(email)).thenReturn(Optional.empty());

        HandlerConfig ex = catchThrowableOfType(() -> service.login(request), HandlerConfig.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getMessage()).isEqualTo("Invalid email or password");
        verifyNoInteractions(jwtService);
    }
}
