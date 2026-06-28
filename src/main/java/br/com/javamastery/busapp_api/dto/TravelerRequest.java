package br.com.javamastery.busapp_api.dto;

import br.com.javamastery.busapp_api.validation.ValidCpf;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TravelerRequest {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotNull(message = "Birth Date is required!")
    @Past(message = "Birth Date must be in the past")
    private LocalDate birthDate;
    @NotBlank(message = "CPF is required!")
    @ValidCpf
    private String cpf;
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
