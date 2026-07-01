package br.com.javamastery.busapp_api.model;

import br.com.javamastery.busapp_api.dto.TripRequest;
import br.com.javamastery.busapp_api.dto.TripUpdateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10,unique = true, nullable = false)
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_city_id",  nullable = false)
    private City origin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_city_id",  nullable = false)
    private City destination;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private BusCompany busCompany;
    @Column(nullable = false)
    private LocalTime departureTime;
    private boolean active = true;
    @Column(nullable = false)
    private double distanceKM;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Trip() {
    }

    public Trip(TripRequest tripRequest, City origin, City destination, BusCompany busCompany) {
        this.origin = origin;
        this.destination = destination;
        this.busCompany = busCompany;
        this.departureTime = tripRequest.getDepartureTime();
        this.price = tripRequest.getPrice();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        generateCode();
        calculateCategory();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateCategory();
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

    public void tripUpdateRequest(TripUpdateRequest tripUpdateRequest, City origin, City destination) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = tripUpdateRequest.getDepartureTime();
        this.price = tripUpdateRequest.getPrice();
    }

    private void calculateCategory(){
        if (this.origin != null && this.destination != null) {
                this.category = this.origin.getState().getUf().equals(this.destination.getState().getUf())
                        ? Category.INTERCITY
                        : Category.INTERSTATE;
        }
    }

    public void deactivate(){
        this.active = false;
    }

}
