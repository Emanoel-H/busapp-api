package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.TravelerRequest;
import br.com.javamastery.busapp_api.dto.TravelerResponse;
import br.com.javamastery.busapp_api.dto.TravelerUpdateRequest;
import br.com.javamastery.busapp_api.dto.TravelerUpdateResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.Traveler;
import br.com.javamastery.busapp_api.repository.TravelerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TravelerService {
    private final TravelerRepository repository;

    public TravelerResponse register(TravelerRequest travelerRequest) {
        if (repository.existsByEmail(travelerRequest.getEmail()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Email already exists");

        if (repository.existsByCpf(travelerRequest.getCpf()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Cpf already exists");

        Traveler traveler = repository.save(new Traveler(travelerRequest));

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

    public TravelerResponse findById(Long id) {
        Traveler traveler = repository.findById(id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "TRAVELER NOT FOUND"));

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

    public TravelerUpdateResponse updateById(Long id, TravelerUpdateRequest travelerUpdateRequest) {
        Traveler traveler = repository.findById(id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "TRAVELER NOT FOUND"));

        traveler.travelerUpdateRequest(travelerUpdateRequest);

        return toUpdateResponse(repository.save(traveler));
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id))
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "TRAVELER NOT FOUND");

        repository.deleteById(id);
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
}
