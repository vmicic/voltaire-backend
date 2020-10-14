package com.voltaire.restaurant.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Restaurant {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String address;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @OneToMany(mappedBy = "restaurant", orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

    @Builder
    private Restaurant(String name, String address, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

}
