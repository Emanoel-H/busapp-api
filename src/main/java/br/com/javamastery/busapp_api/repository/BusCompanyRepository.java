package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.BusCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusCompanyRepository extends JpaRepository<BusCompany, Long> {
    
}
