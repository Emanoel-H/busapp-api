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
    Long state_code;
    String uf;
    String name;
    String region;
}
