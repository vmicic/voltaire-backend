package com.voltaire.restaurant;

import com.voltaire.error.customerrors.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    public MenuItem createMenuItem(MenuItemDto menuItemDto) {
        if(restaurantService.notExists(menuItemDto.getRestaurantId())) {
            throw new EntityNotFoundException(Restaurant.class, "id", menuItemDto.getRestaurantId().toString());
        }

        var menuItem = MenuItem.builder()
                .name(menuItemDto.getName())
                .price(menuItemDto.getPrice())
                .description(menuItemDto.getDescription())
                .restaurant(restaurantService.findById(menuItemDto.getRestaurantId()))
                .build();

        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem findById(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if(menuItem.isEmpty()) {
            throw new EntityNotFoundException(MenuItem.class, "id", id.toString());
        }

        return menuItem.get();
    }

    public boolean notExists(Long id) {
        return !menuItemRepository.existsById(id);
    }

    public void updateMenuItem(Long id, MenuItem menuItem) {
        if(notExists(id)) {
            throw new EntityNotFoundException(MenuItem.class, "id", id.toString());
        }

        menuItem.setId(id);
        menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        if(notExists(id)) {
            throw new EntityNotFoundException(MenuItem.class, "id", id.toString());
        }

        menuItemRepository.deleteById(id);
    }

}
