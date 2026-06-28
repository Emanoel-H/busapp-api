package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TravelerResponse {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;
    private int age;
    private String email;
    private String telephone;
    private BigDecimal creditsBalance;
    private LocalDateTime createdAt;
}
