package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StateResponse {
    private Long code;
    private String uf;
    private String name;
    private String region;
}
