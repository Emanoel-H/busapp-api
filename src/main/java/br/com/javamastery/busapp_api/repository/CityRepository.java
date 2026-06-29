package br.com.javamastery.busapp_api.repository;

import br.com.javamastery.busapp_api.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City,Long> {
    @Query("SELECT c FROM City c JOIN FETCH c.state WHERE c.state.uf = :uf ORDER BY c.city ")
    List<City> findByStateUf(@Param("uf") String uf);

    @Query("SELECT c FROM City c JOIN FETCH c.state WHERE LOWER(c.city) LIKE LOWER(CONCAT(:name, '%')) ORDER BY c.city")
    List<City> findByNameStartingWith(@Param("name") String name);

    @Query("SELECT c FROM City c JOIN FETCH c.state WHERE c.ibgeCode = :ibgeCode")
    Optional<City> findByIBGECode(@Param("ibgeCode") Long ibgeCode);
}
