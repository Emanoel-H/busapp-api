package br.com.javamastery.busapp_api.client;

import br.com.javamastery.busapp_api.client.dto.OsrmResponse;
import br.com.javamastery.busapp_api.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class OsrmClient {
    public static final String BASE_URL = "http://router.project-osrm.org/route/v1/driving/";
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public double getRealDistanceKM(City origin, City destination) {
        String url = BASE_URL + origin.getLongitude() + "," + origin.getLatitude() +
                ";" +  destination.getLongitude() + "," + destination.getLatitude() +
                "?overview=false";

        try{
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String>  response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Osrm API error: " + response.statusCode());

            return objectMapper.readValue(response.body(), OsrmResponse.class).getDistanceInKm();
        }catch(Exception e){
            System.out.println("Could not fetch real distance, falling back to Haversine.");
            return haversine(origin, destination);
        }
    }


    private double haversine(City origin, City destination) {
        final int R = 6371;
        double latDistance = Math.toRadians(destination.getLatitude() - origin.getLatitude());
        double lonDistance = Math.toRadians(destination.getLongitude() - origin.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(origin.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
