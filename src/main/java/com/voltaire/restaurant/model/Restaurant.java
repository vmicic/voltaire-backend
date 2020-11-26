package com.voltaire.restaurant.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.voltaire.shared.Geolocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Restaurant {

    @DocumentId
    private String id;

    private String name;

    private String address;

    private String description;

    private String openingTime;

    private String closingTime;

    private Geolocation geolocation;
}
