package br.com.javamastery.busapp_api.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmResponse {
    private String code;
    private List<OsrmRoute> routes;

    public double getDistanceInKm(){
        if(routes==null || routes.isEmpty())
            throw new RuntimeException("No routes found in OsrmResponse");
        return routes.getFirst().getDistance() / 1000;
    }
}
