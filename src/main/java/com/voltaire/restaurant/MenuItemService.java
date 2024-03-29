package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItem createMenuItem(MenuItemDto menuItemDto) {
        var menuItem = MenuItem.builder()
                .name(menuItemDto.getName())
                .price(menuItemDto.getPrice())
                .description(menuItemDto.getDescription())
                .restaurant(findRestaurantById(menuItemDto.getRestaurantId()))
                .build();

        return menuItemRepository.save(menuItem);
    }

    private Restaurant findRestaurantById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }


    public boolean notExists(UUID id) {
        return !menuItemRepository.existsById(id);
    }

    public IdResponse updateMenuItem(UUID id, MenuItem menuItem) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id.toString());
        }

        menuItem.setId(id);
        return new IdResponse(menuItemRepository.save(menuItem).getId());
    }

    public IdResponse deleteMenuItem(UUID id) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id.toString());
        }

        menuItemRepository.deleteById(id);
        return new IdResponse(id);
    }

    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem findById(UUID id) {
        return menuItemRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

}
