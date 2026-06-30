package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t JOIN FETCH t.origin oc JOIN FETCH oc.state JOIN FETCH t.destination dc JOIN FETCH dc.state JOIN FETCH t.busCompany bc WHERE t.code = :code")
    Optional<Trip> findByCode(@Param("code") String code);

    @Query("SELECT t FROM Trip t JOIN FETCH t.origin oc JOIN FETCH oc.state JOIN FETCH t.destination dc JOIN FETCH dc.state JOIN FETCH t.busCompany bc WHERE t.active = true")
    List<Trip> findAllActive();

    boolean existsByCode(String code);

    @Query("SELECT t FROM Trip t JOIN FETCH t.origin oc JOIN FETCH oc.state JOIN FETCH t.destination dc JOIN FETCH dc.state JOIN FETCH t.busCompany bc WHERE bc.id = :busCompanyId AND t.active = true")
    List<Trip> findAllByBusCompanyId(@Param("busCompanyId") Long busCompanyId);

    @Query("SELECT t FROM Trip t JOIN FETCH t.origin oc JOIN FETCH oc.state JOIN FETCH t.destination dc JOIN FETCH dc.state JOIN FETCH t.busCompany bc WHERE oc.ibgeCode = :originCode AND dc.ibgeCode = :destinationCode AND t.active = true")
    List<Trip> findAllByOriginAndDestination(@Param("originCode") Long originCode, @Param("destinationCode") Long destinationCode);
}
