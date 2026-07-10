package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.*;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TravelerService {
    private final TravelerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public TravelerResponse register(TravelerRequest travelerRequest) {
        if (repository.existsByEmail(travelerRequest.getEmail()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Email already exists");

        if (repository.existsByCpf(travelerRequest.getCpf()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Cpf already exists");

        String password = passwordEncoder.encode(travelerRequest.getPassword());

        return toResponse(repository.save(new Traveler(travelerRequest, password)));
    }

    public TravelerResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public TravelerUpdateResponse updateById(Long id, TravelerUpdateRequest travelerUpdateRequest) {
        Traveler traveler = findOrThrow(id);

        traveler.travelerUpdateRequest(travelerUpdateRequest);

        return toUpdateResponse(repository.save(traveler));
    }

    public void deleteById(Long id) {
        Traveler traveler = findOrThrow(id);
        repository.delete(traveler);
    }

    public TravelerCreditsResponse addCredits(Long id, BigDecimal credits) {
        if (credits == null || credits.compareTo(BigDecimal.ZERO) <= 0)
            throw new HandlerConfig(HttpStatus.BAD_REQUEST, "CREDIT AMOUNT MUST BE GREATER THAN ZERO");

        Traveler traveler = findOrThrow(id);

        traveler.addCredits(credits);

        return toCreditsResponse(repository.save(traveler));
    }

    public TravelerUpdateResponse toUpdateResponse(Traveler  traveler) {
        return TravelerUpdateResponse.builder()
                .id(traveler.getId())
                .name(traveler.getName())
                .cpf(traveler.getCpf())
                .birthDate(traveler.getBirthDate())
                .age(traveler.getAge())
                .updatedAt(traveler.getUpdatedAt())
                .build();
    }

    public TravelerResponse toResponse(Traveler traveler) {
        return TravelerResponse.builder()
                .id(traveler.getId())
                .name(traveler.getName())
                .cpf(traveler.getCpf())
                .birthDate(traveler.getBirthDate())
                .age(traveler.getAge())
                .email(traveler.getEmail())
                .telephone(traveler.getTelephone())
                .creditsBalance(traveler.getCreditsBalance())
                .createdAt(traveler.getCreatedAt())
                .build();
    }

    public Traveler findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "TRAVELER NOT FOUND"));
    }

    public TravelerCreditsResponse toCreditsResponse(Traveler traveler) {
        return TravelerCreditsResponse.builder()
                .id(traveler.getId())
                .creditsBalance(traveler.getCreditsBalance())
                .updatedAt(traveler.getUpdatedAt())
                .build();
    }
}
