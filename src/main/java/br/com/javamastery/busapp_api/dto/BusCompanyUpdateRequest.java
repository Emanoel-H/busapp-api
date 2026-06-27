package br.com.javamastery.busapp_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusCompanyUpdateRequest {
    @NotBlank(message = "Legal Name is required!")
    private String legalName;
    @NotBlank(message = "Trading Name is required!")
    private String tradingName;
    @NotBlank(message = "CNPJ is required!")
    @Pattern(regexp = "\\d{14}", message = "CNPJ must contain 14 digits!")
    private String cnpj;
    @Pattern(regexp = "\\d{10,11}", message = "Telephone must contain 10 or 11 digits!")
    @NotBlank(message = "Telephone is required!")
    private String telephone;
}
