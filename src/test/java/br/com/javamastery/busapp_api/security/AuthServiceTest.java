package br.com.javamastery.busapp_api.security;

import br.com.javamastery.busapp_api.dto.LoginRequest;
import br.com.javamastery.busapp_api.dto.LoginResponse;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(jwtService.generateToken("empresa@teste.com", "COMPANY")).thenReturn("token-company");

        LoginResponse response = service.login(request);

        assertThat(response.getRole()).isEqualTo("COMPANY");
        assertThat(response.getToken()).isEqualTo("token-company");
        assertThat(response.getEmail()).isEqualTo("empresa@teste.com");
        verifyNoInteractions(travelerRepository);
    }

}
