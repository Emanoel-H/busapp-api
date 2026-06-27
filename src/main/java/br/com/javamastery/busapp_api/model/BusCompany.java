package br.com.javamastery.busapp_api.model;

import br.com.javamastery.busapp_api.dto.BusCompanyRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bus_company")
@Getter
@Setter
public class BusCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String legalName;
    @Column(nullable = false)
    private String tradingName;
    @Column(nullable = false,  unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false,  unique = true)
    private String email;
    @Column(nullable = false, length = 16)
    private String password;
    @Column(nullable = false,  length = 11)
    private String telephone;
    @Column(nullable = false,  updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BusCompany() {
    }

    public BusCompany(BusCompanyRequest busCompanyRequest) {
        this.legalName = busCompanyRequest.getLegalName();
        this.tradingName = busCompanyRequest.getTradingName();
        this.cnpj = busCompanyRequest.getCnpj();
        this.email = busCompanyRequest.getEmail();
        this.password = busCompanyRequest.getPassword();
        this.telephone = busCompanyRequest.getTelephone();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
