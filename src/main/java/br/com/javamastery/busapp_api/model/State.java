package br.com.javamastery.busapp_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "states")
@Getter
public class State {
    @Id
    @Column(name = "state_code")
    private Long code;
    private String uf;
    private String name;
    private String region;
}
