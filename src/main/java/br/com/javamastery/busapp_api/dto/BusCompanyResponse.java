package br.com.javamastery.busapp_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusCompanyResponse {
    private Long id;
    private String legalName;
    private String tradingName;
    private String cnpj;
    private String telephone;
    private String email;
    private LocalDateTime createdAt;
}
