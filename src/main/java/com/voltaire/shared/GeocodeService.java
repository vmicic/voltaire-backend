package com.voltaire.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltaire.exception.customexceptions.BadRequestException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodeService {

    @Value("${google.maps.api-key}")
    private String geocodeApiKey;

    @Value("${google.maps.geocode.url}")
    private String geocodeUrl;

    public Geolocation getGeolocationForAddressString(String address) {
        var addressFormatted = address.trim().replace(" ", "+");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(geocodeUrl + addressFormatted + "&key=" + geocodeApiKey, String.class);
        checkGeocodeResponseStatus(responseEntity);

        return readGeolocationFromGeocodeResponseEntity(responseEntity);
    }

    @SneakyThrows
    private void checkGeocodeResponseStatus(ResponseEntity<String> responseEntity) {
        ObjectMapper mapper = new ObjectMapper();
        var responseJson = mapper.readTree(responseEntity.getBody());
        String geocodeStatus = responseJson.get("status").asText();

        if (geocodeStatus.equals("ZERO_RESULTS")) {
            throw new BadRequestException("Address is invalid");
        }

        if (!geocodeStatus.equals("OK")) {
            throw new BadRequestException("Couldn't retrieve location for requested address.");
        }
    }

    @SneakyThrows
    private Geolocation readGeolocationFromGeocodeResponseEntity(ResponseEntity<String> responseEntity) {
        ObjectMapper mapper = new ObjectMapper();
        var responseJson = mapper.readTree(responseEntity.getBody());
        var longitudeNode = responseJson.get("results").get(0).get("geometry").get("location").get("lng");
        var latitudeNode = responseJson.get("results").get(0).get("geometry").get("location").get("lat");

        var longitude = Double.parseDouble(String.valueOf(longitudeNode));
        var latitude = Double.parseDouble(String.valueOf(latitudeNode));
        return new Geolocation(longitude, latitude);
    }

    public double distance(Geolocation geolocation1, Geolocation geolocation2) {

        final int R = 6371; // Radius of the earth

        var latDistance = Math.toRadians(geolocation2.getLatitude() - geolocation1.getLatitude());
        var lonDistance = Math.toRadians(geolocation2.getLongitude() - geolocation1.getLongitude());
        var a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(geolocation1.getLatitude())) * Math.cos(Math.toRadians(geolocation2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var distance = R * c * 1000; // convert to meters

        var height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
