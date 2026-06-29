package br.com.javamastery.busapp_api.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "cities")
@Getter
public class City {
    @Id
    @Column(name = "ibge_code")
    Long ibgeCode;
    String city;
    double latitude;
    double longitude;
    int ddd;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_code")
    State state;

}
