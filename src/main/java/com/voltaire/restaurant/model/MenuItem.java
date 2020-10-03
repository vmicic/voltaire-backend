package com.voltaire.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
public class MenuItem {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    private MenuItem(MenuItemBuilder menuItemBuilder) {
        this.name = menuItemBuilder.name;
        this.price = menuItemBuilder.price;
        this.description = menuItemBuilder.description;
        this.restaurant = menuItemBuilder.restaurant;
    }

    public static class MenuItemBuilder {
        private String name;
        private BigDecimal price;
        private String description;
        private Restaurant restaurant;

        public MenuItemBuilder() {

        }

        public MenuItemBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MenuItemBuilder setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public MenuItemBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public MenuItemBuilder setRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public MenuItem build() {
            return new MenuItem(this);
        }
    }
}
