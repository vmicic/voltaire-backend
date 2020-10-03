package com.voltaire.restaurant;

import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.model.MenuItem.MenuItemBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantService restaurantService) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantService = restaurantService;
    }

    public MenuItem createMenuItem(MenuItemDto menuItemDto) {
        var menuItem = new MenuItemBuilder()
                .setName(menuItemDto.getName())
                .setPrice(menuItemDto.getPrice())
                .setDescription(menuItemDto.getDescription())
                .setRestaurant(restaurantService.findById(menuItemDto.getRestaurantId()))
                .build();

        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id).orElse(null);
    }

    public boolean notExists(Long id) {
        return !menuItemRepository.existsById(id);
    }

    public void updateMenuItem(Long id, MenuItem menuItem) {
        menuItem.setId(id);
        menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

}
