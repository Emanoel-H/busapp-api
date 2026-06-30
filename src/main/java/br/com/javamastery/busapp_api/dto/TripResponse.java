package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class TripResponse {
    private Long id;
    private String code;
    private String originCity;
    private String originState;
    private String destinationCity;
    private String destinationState;
    private String busCompany;
    private BigDecimal price;
    private LocalTime departureTime;
    private double distanceKM;
    private String category;
    private boolean active;
}
