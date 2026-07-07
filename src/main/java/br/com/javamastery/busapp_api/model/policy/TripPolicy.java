package br.com.javamastery.busapp_api.model.policy;

import br.com.javamastery.busapp_api.client.OsrmClient;
import br.com.javamastery.busapp_api.model.City;
import org.springframework.stereotype.Component;

@Component
public class TripPolicy {
    public double calculateDistance(City origin, City destination, OsrmClient osrmClient) {
        return osrmClient.getRealDistanceKM(origin, destination);
    }
}
