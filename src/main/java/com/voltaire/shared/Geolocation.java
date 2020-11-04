package com.voltaire.shared;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geolocation {

    @DocumentId
    private String id;

    private Double longitude;

    private Double latitude;

    public Geolocation(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
