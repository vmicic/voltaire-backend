package com.voltaire.integration;

import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Point;
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
    void getPointForAddressString() {
        var address = "Puskinova 5, Novi Sad";
        var expectedPoint = new Point(19.8317921, 45.2458004);

        var point1 = geocodeService.getPointForAddressString(address);

        assertEquals(expectedPoint, point1);
    }

    @Test
    void distanceTest() {
        Point point1 = new Point(19.8317921, 45.2458004);
        Point point2 = new Point(19.8493474, 45.2595586);

        var distance = geocodeService.distance(point1, point2);

        assertEquals(2056.424499123431, distance);
    }
}
