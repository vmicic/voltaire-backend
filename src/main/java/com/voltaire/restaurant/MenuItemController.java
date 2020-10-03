package com.voltaire.restaurant;

import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    public MenuItemController(MenuItemService menuItemService, RestaurantService restaurantService) {
        this.menuItemService = menuItemService;
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<?> createMenuItem(@RequestBody MenuItemDto menuItemDTO) {
        if(restaurantService.notExists(menuItemDTO.getRestaurantId())) {
            return new ResponseEntity<>("Restaurant doesn't exist", HttpStatus.NOT_FOUND);
        }

        MenuItem menuItem = menuItemService.createMenuItem(menuItemDTO);

        return new ResponseEntity<>(menuItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItemById(@PathVariable Long id) {
        if(menuItemService.notExists(id)) {
            return new ResponseEntity<>("Requested menu item not found", HttpStatus.NOT_FOUND);
        }

        MenuItem menuItem = menuItemService.findById(id);

        return new ResponseEntity<>(menuItem, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        if(menuItemService.notExists(id)) {
            return new ResponseEntity<>("Requested menu item not found", HttpStatus.NOT_FOUND);
        }

        menuItemService.updateMenuItem(id, menuItem);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        if(menuItemService.notExists(id)) {
            return new ResponseEntity<>("Requested menu item not found", HttpStatus.NOT_FOUND);
        }

        menuItemService.deleteMenuItem(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
