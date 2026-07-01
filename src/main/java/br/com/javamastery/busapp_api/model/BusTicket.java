package br.com.javamastery.busapp_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_ticket")
@Getter
@Setter
public class BusTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, unique = true, nullable = false)
    private String code;
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traveler_id", nullable = false)
    private Traveler traveler;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    @Column(nullable = false)
    private LocalDate departureDate;
    @Column(nullable = false, updatable = false)
    private LocalDate saleDate;
    private LocalDateTime cancelDate;
    private boolean canceled = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        this.saleDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();

        if (this.price == null)
            this.price = this.trip.getPrice();

        generateCode();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private void generateCode(){
        if(this.code == null){
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder stringBuilder = new StringBuilder();
            java.util.Random random = new java.util.Random();

            while(stringBuilder.length() < 10)
                stringBuilder.append(chars.charAt(random.nextInt(chars.length())));

            this.code = stringBuilder.toString();

        }
    }
}
