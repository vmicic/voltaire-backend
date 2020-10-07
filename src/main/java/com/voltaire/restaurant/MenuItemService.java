package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    public MenuItem createMenuItem(MenuItemDto menuItemDto) {
        var menuItem = MenuItem.builder()
                .name(menuItemDto.getName())
                .price(menuItemDto.getPrice())
                .description(menuItemDto.getDescription())
                .restaurant(restaurantService.findById(menuItemDto.getRestaurantId()))
                .build();

        return menuItemRepository.save(menuItem);
    }

    public boolean notExists(Long id) {
        return !menuItemRepository.existsById(id);
    }

    public IdResponse updateMenuItem(Long id, MenuItem menuItem) {
        if (notExists(id)) {
            throw new EntityNotFoundException(MenuItem.class, "id", id.toString());
        }

        menuItem.setId(id);
        return new IdResponse(menuItemRepository.save(menuItem).getId());
    }

    public IdResponse deleteMenuItem(Long id) {
        if (notExists(id)) {
            throw new EntityNotFoundException(MenuItem.class, "id", id.toString());
        }

        menuItemRepository.deleteById(id);
        return new IdResponse(id);
    }

    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MenuItem.class, "id", id.toString()));
    }

}
