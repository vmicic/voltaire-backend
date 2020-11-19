package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.CreateMenuItemRequest;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MenuItemService {


    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public void createMenuItem(CreateMenuItemRequest createMenuItemRequest) {
        if (restaurantRepository.notExists(createMenuItemRequest.getRestaurantId())) {
            throw new EntityNotFoundException("id", createMenuItemRequest.getRestaurantId());
        }

        var menuItem = MenuItem.builder()
                .id(UUID.randomUUID().toString())
                .name(createMenuItemRequest.getName())
                .price(createMenuItemRequest.getPrice())
                .description(createMenuItemRequest.getDescription())
                .restaurantId(createMenuItemRequest.getRestaurantId())
                .build();

        menuItemRepository.save(menuItem);
    }

    public boolean notExists(String id) {
        return menuItemRepository.notExistsById(id);
    }

    public IdResponse updateMenuItem(String id, MenuItem menuItem) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id);
        }

        menuItem.setId(id);
        return new IdResponse(id);
    }

    public IdResponse deleteMenuItem(String id) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id);
        }

        menuItemRepository.deleteById(id);
        return new IdResponse(id);
    }

    public MenuItem findById(String id) {
        return menuItemRepository.findById(id);
    }


}
