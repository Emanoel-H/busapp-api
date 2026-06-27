package br.com.javamastery.busapp_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusCompanyRequest {
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
    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 16, message = "Password must be between 6 and 16 characters")
    private String password;
}
