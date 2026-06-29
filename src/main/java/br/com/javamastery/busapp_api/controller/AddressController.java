package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.CityResponse;
import br.com.javamastery.busapp_api.dto.StateResponse;
import br.com.javamastery.busapp_api.exception.HandlerConfig;
import br.com.javamastery.busapp_api.model.City;
import br.com.javamastery.busapp_api.model.State;
import br.com.javamastery.busapp_api.repository.CityRepository;
import br.com.javamastery.busapp_api.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private CityRepository cityRepository;
    private StateRepository stateRepository;

    @GetMapping("/states")
    public ResponseEntity<List<StateResponse>> listStates(){
        return ResponseEntity.ok(stateRepository.findAll().stream().map(this::toStateResponse).toList());
    }

    @GetMapping("/states/{uf}/cities")
    public ResponseEntity<List<CityResponse>> listCitiesByState(@PathVariable String uf){
        List<City> cities = cityRepository.findByStateUf(uf);

        if(cities.isEmpty())
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "Cities not found for uf:" + uf);

        return ResponseEntity.ok(cities.stream().map(this::toCityResponse).toList());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityResponse>> searchCities(@RequestParam String city){
        List<City> cities = cityRepository.findByNameStartingWith((city));

        if(cities.isEmpty())
            throw new HandlerConfig(HttpStatus.NOT_FOUND, "Cities not found for name:" + city);

        return ResponseEntity.ok(cities.stream().map(this::toCityResponse).toList());
    }

    public StateResponse toStateResponse(State state){
        return StateResponse.builder()
                .code(state.getCode())
                .uf(state.getUf())
                .name(state.getName())
                .region(state.getRegion())
                .build();
    }

    public CityResponse toCityResponse(City city){
        return CityResponse.builder()
                .ibgeCode(city.getIbgeCode())
                .city(city.getCity())
                .state(city.getState())
                .ddd(city.getDdd())
                .build();
    }
}
