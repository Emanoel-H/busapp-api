package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.LoginRequest;
import br.com.javamastery.busapp_api.dto.LoginResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BusCompanyRepository busCompanyRepository;
    private final TravelerRepository travelerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        return busCompanyRepository.findByEmail(request.getEmail())
                .filter(c -> passwordEncoder.matches(request.getPassword(), c.getPassword()))
                .map(this::companyToResponse)
                .orElseGet(() -> travelerRepository.findByEmail(request.getEmail())
                        .filter(t -> passwordEncoder.matches(request.getPassword(), t.getPassword()))
                        .map(this::travelerToResponse)
                        .orElseThrow(() -> new HandlerConfig(HttpStatus.UNAUTHORIZED, "Invalid email or password")));
    }

    public LoginResponse travelerToResponse(Traveler traveler){
        return LoginResponse.builder()
                .token(jwtService.generateToken(traveler.getEmail(), "TRAVELER"))
                .email(traveler.getEmail())
                .role("TRAVELER")
                .build();
    }

    public LoginResponse companyToResponse(BusCompany company){
        return LoginResponse.builder()
                .token(jwtService.generateToken(company.getEmail(), "COMPANY"))
                .email(company.getEmail())
                .role("COMPANY")
                .build();
    }
}
