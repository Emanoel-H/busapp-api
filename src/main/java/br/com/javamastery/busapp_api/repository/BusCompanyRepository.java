package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.BusCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusCompanyRepository extends JpaRepository<BusCompany, Long> {
    Optional<BusCompany> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);
}
