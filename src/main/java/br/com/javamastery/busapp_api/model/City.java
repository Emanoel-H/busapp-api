package br.com.javamastery.busapp_api.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "cities")
@Getter
public class City {
    @Id
    @Column(name = "ibge_code")
    private Long ibgeCode;
    private String city;
    private double latitude;
    private double longitude;
    private int ddd;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_code")
    private State state;

}
