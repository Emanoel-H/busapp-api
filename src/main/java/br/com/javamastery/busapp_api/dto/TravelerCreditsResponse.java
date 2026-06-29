package br.com.javamastery.busapp_api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TravelerCreditsResponse {
    private Long id;
    private BigDecimal creditsBalance;
    private LocalDateTime updatedAt;
}
