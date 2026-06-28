package br.com.javamastery.busapp_api.dto;

import br.com.javamastery.busapp_api.validation.ValidCpf;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TravelerUpdateRequest {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Birth Date is required!")
    @Past(message = "Birth Date must be in the past")
    private LocalDate birthDate;
    @NotBlank(message = "CPF is required!")
    @ValidCpf
    private String cpf;
    @Pattern(regexp = "\\d{10,11}", message = "Telephone must contain 10 or 11 digits!")
    @NotBlank(message = "Telephone is required!")
    private String telephone;
}
