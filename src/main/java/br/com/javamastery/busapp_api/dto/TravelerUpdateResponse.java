package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TravelerUpdateResponse {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;
    private int age;
    private LocalDateTime updatedAt;
}
