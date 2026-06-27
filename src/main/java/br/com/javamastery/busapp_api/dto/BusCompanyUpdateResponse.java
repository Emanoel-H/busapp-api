package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BusCompanyUpdateResponse {
    private Long id;
    private String legalName;
    private String tradingName;
    private String cnpj;
    private String telephone;
}
