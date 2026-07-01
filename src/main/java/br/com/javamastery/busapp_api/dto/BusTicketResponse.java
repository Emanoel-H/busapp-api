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
public class BusTicketResponse {
    private String code;
    private BigDecimal price;
    private LocalDate departureDate;
    private String travelerName;
    private String cpf;
    private String originCity;
    private String originState;
    private String destinationCity;
    private String destinationState;
    private LocalDate saleDate;
    private LocalDateTime createdAt;
}
