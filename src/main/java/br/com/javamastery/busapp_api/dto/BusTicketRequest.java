package br.com.javamastery.busapp_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BusTicketRequest {
    @NotNull(message = "Traveler id is required!")
    private Long traveler_id;
    @NotNull(message = "Trip code is required!")
    private String trip_code;
    @NotNull(message = "Departure Date is required!")
    private LocalDate departureDate;
}
