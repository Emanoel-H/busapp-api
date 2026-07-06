package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.LoginResponse;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import br.com.javamastery.busapp_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BusCompanyRepository busCompanyRepository;
    private final TravelerRepository travelerRepository;
    private final JwtService jwtService;

    public LoginResponse travelerToResponse(Traveler traveler){
        return LoginResponse.builder()
                .token(jwtService.generateToken(traveler.getEmail(), "TRAVELER"))
                .email(traveler.getEmail())
                .role("TRAVELER")
                .build();
    }
}
