package com.voltaire.restaurant.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @OneToMany(mappedBy = "restaurant", orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

}
