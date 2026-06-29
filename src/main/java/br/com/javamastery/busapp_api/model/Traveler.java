package br.com.javamastery.busapp_api.model;

import br.com.javamastery.busapp_api.dto.TravelerRequest;
import br.com.javamastery.busapp_api.dto.TravelerUpdateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "traveler")
@Getter
@Setter
public class Traveler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false,  unique = true, length = 11)
    private String cpf;
    @Formula("extract(year from age(current_date, birth_date))")
    private int age;
    @Column(nullable = false,  unique = true)
    private String email;
    @Column(nullable = false, length = 16)
    private String password;
    @Column(nullable = false,  length = 11)
    private String telephone;
    @Column(precision = 10, scale = 2)
    private BigDecimal creditsBalance;
    @Column(nullable = false,  updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Traveler() {
    }

    public Traveler(TravelerRequest  travelerRequest) {
        this.name = travelerRequest.getName();
        this.birthDate = travelerRequest.getBirthDate();
        this.cpf = travelerRequest.getCpf();
        this.email = travelerRequest.getEmail();
        this.password = travelerRequest.getPassword();
        this.telephone = travelerRequest.getTelephone();
    }

    public void travelerUpdateRequest(TravelerUpdateRequest travelerUpdateRequest) {
        this.name = travelerUpdateRequest.getName();
        this.birthDate = travelerUpdateRequest.getBirthDate();
        this.cpf = travelerUpdateRequest.getCpf();
        this.telephone = travelerUpdateRequest.getTelephone();
    }

    public void addCredits(BigDecimal credits) {
        this.creditsBalance = this.creditsBalance.add(credits);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.creditsBalance == null)
            this.creditsBalance = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
