package br.com.javamastery.busapp_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
public class TripUpdateRequest {
    @NotNull(message = "Origin City IBGE code is required!")
    private Long originCityIbgeCode;
    @NotNull(message = "Destination City IBGE code is required!")
    private Long destinationCityIbgeCode;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero!")
    private BigDecimal price;
    @NotNull(message = "Departure time is required! (Use 24-hour format, example: 14:30)")
    private LocalTime departureTime;
}
