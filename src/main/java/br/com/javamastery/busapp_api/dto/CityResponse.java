package br.com.javamastery.busapp_api.dto;

import br.com.javamastery.busapp_api.model.State;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CityResponse {
    private Long ibgeCode;
    private String city;
    private String state;
    private int ddd;
}
