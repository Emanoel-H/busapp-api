package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State,Long> {
    Optional<State> findByUf(String uf);
    Optional<State> findByNameIgnoreCase(String name);
}
