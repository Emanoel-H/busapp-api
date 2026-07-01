package br.com.javamastery.busapp_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BusTicketRequest {
    @NotNull(message = "Traveler id is required!")
    private Long traveler_id;
    @NotNull(message = "Trip code is required!")
    private String trip_code;
    @NotNull(message = "Departure Date is required!")
    @Future(message = "Departure Date must be in the future!")
    private LocalDate departureDate;
}
