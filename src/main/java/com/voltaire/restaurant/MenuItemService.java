package com.voltaire.restaurant;

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

    public MenuItem createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        Restaurant restaurant = restaurantService.findById(menuItemDTO.getRestaurantId());
        menuItem.setRestaurant(restaurant);

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
