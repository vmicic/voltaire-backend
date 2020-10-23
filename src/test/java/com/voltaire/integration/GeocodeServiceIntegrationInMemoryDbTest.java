package com.voltaire.integration;

import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Geolocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(profiles = "inmemorydb-test")
class GeocodeServiceIntegrationInMemoryDbTest {

    @Autowired
    GeocodeService geocodeService;

    @Test
    void getGeolocationForAddressString() {
        var address = "Puskinova 5, Novi Sad";
        var expectedGeolocation = new Geolocation(19.8317921, 45.2458004);

        var geolocation1 = geocodeService.getGeolocationForAddressString(address);

        assertEquals(expectedGeolocation, geolocation1);
    }

    @Test
    void distanceTest() {
        Geolocation geolocation1 = new Geolocation(19.8317921, 45.2458004);
        Geolocation geolocation2 = new Geolocation(19.8493474, 45.2595586);

        var distance = geocodeService.distance(geolocation1, geolocation2);

        assertEquals(2056.424499123431, distance);
    }
}
