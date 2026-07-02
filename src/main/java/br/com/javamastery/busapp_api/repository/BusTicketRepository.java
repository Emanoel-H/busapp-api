package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.BusTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusTicketRepository extends JpaRepository<BusTicket, Long> {
    @Query("SELECT bt FROM BusTicket bt " +
            "JOIN FETCH bt.traveler " +
            "JOIN FETCH bt.trip t " +
            "JOIN FETCH t.origin oc " +
            "JOIN FETCH oc.state " +
            "JOIN FETCH t.destination dc " +
            "JOIN FETCH dc.state " +
            "JOIN FETCH t.busCompany " +
            "WHERE bt.canceled = false")
    Optional<BusTicket> findByCode(String code);

    @Query("SELECT bt FROM BusTicket bt " +
            "JOIN FETCH bt.traveler " +
            "JOIN FETCH bt.trip t " +
            "JOIN FETCH t.origin oc " +
            "JOIN FETCH oc.state " +
            "JOIN FETCH t.destination dc " +
            "JOIN FETCH dc.state " +
            "JOIN FETCH t.busCompany " +
            "WHERE bt.canceled = false " +
            "AND bt.traveler.id = :travelerId")
    List<BusTicket> findAllActiveByTravelerId(@Param("travelerId") Long travelerId);

    @Query("SELECT bt FROM BusTicket bt " +
            "JOIN FETCH bt.traveler " +
            "JOIN FETCH bt.trip t " +
            "JOIN FETCH t.origin oc " +
            "JOIN FETCH oc.state " +
            "JOIN FETCH t.destination dc " +
            "JOIN FETCH dc.state " +
            "JOIN FETCH t.busCompany " +
            "WHERE bt.traveler.id = :travelerId")
    List<BusTicket> findAllByTravelerId(@Param("travelerId")Long travelerId);

    boolean existsByCode(String code);

    boolean existsByTripId(Long tripId);
}
