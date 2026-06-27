package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyResponse;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BusCompanyService {
    private final BusCompanyRepository repository;

    public BusCompanyResponse register(BusCompanyRequest busCompanyRequest) {
        if (repository.existsByEmail(busCompanyRequest.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");

        if (repository.existsByCnpj(busCompanyRequest.getCnpj()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cnpj already exists");

        BusCompany busCompany = repository.save(new BusCompany(busCompanyRequest));

        return BusCompanyResponse.builder()
                .id(busCompany.getId())
                .legalName(busCompany.getLegalName())
                .tradingName(busCompany.getTradingName())
                .cnpj(busCompany.getCnpj())
                .telephone(busCompany.getTelephone())
                .email(busCompany.getEmail())
                .createdAt(busCompany.getCreatedAt())
                .build();
    }
}
