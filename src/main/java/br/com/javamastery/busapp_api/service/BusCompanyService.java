package br.com.javamastery.busapp_api.service;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyResponse;
import br.com.javamastery.busapp_api.dto.BusCompanyUpdateRequest;
import br.com.javamastery.busapp_api.dto.BusCompanyUpdateResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.BusCompany;
import br.com.javamastery.busapp_api.repository.BusCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusCompanyService {
    private final BusCompanyRepository repository;

    public BusCompanyResponse register(BusCompanyRequest busCompanyRequest) {
        if (repository.existsByEmail(busCompanyRequest.getEmail()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Email already exists");

        if (repository.existsByCnpj(busCompanyRequest.getCnpj()))
            throw new HandlerConfig(HttpStatus.CONFLICT, "Cnpj already exists");

        return toResponse(repository.save(new BusCompany(busCompanyRequest)));
    }

    public BusCompanyResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public BusCompanyUpdateResponse update(Long id, BusCompanyUpdateRequest busCompanyUpdateRequest) {
        BusCompany busCompany = findOrThrow(id);

        busCompany.busCompanyUpdateRequest(busCompanyUpdateRequest);

        return toUpdateResponse(repository.save(busCompany));
    }

    public void deleteById(Long id){
        BusCompany  busCompany = findOrThrow(id);
        repository.delete(busCompany);
    }

    public BusCompanyUpdateResponse toUpdateResponse(BusCompany busCompany){
        return BusCompanyUpdateResponse.builder()
                .id(busCompany.getId())
                .legalName(busCompany.getLegalName())
                .tradingName(busCompany.getTradingName())
                .cnpj(busCompany.getCnpj())
                .telephone(busCompany.getTelephone())
                .updatedAt(busCompany.getUpdatedAt())
                .build();
    }

    public BusCompanyResponse toResponse(BusCompany busCompany){
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

    public BusCompany findOrThrow(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new HandlerConfig(HttpStatus.NOT_FOUND, "COMPANY NOT FOUND"));
    }
}
