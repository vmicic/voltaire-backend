package com.voltaire.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodeService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    @Value("${google.maps.geocode.url}")
    private String geocodeUrl;

    public Point getPointForAddressString(String address) {
        var addressFormatted = address.trim().replace(" ", "+");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(geocodeUrl + addressFormatted + "&key=" + apiKey, String.class);

        return readPointFromGeocodeResponseEntity(responseEntity);
    }

    @SneakyThrows
    public Point readPointFromGeocodeResponseEntity(ResponseEntity<String> responseEntity) {
        ObjectMapper mapper = new ObjectMapper();
        var responseJson = mapper.readTree(responseEntity.getBody());
        var longitudeNode = responseJson.get("results").get(0).get("geometry").get("location").get("lng");
        var latitudeNode = responseJson.get("results").get(0).get("geometry").get("location").get("lat");

        var longitude = Double.parseDouble(String.valueOf(longitudeNode));
        var latitude = Double.parseDouble(String.valueOf(latitudeNode));
        return new Point(longitude, latitude);
    }

    public double distance(Point point1, Point point2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(point2.getLatitude() - point1.getLatitude());
        double lonDistance = Math.toRadians(point2.getLongitude() - point1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(point1.getLatitude())) * Math.cos(Math.toRadians(point2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
