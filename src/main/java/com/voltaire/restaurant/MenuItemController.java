package com.voltaire.restaurant;

import com.voltaire.restaurant.model.CreateMenuItemRequest;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMenuItem(@RequestBody CreateMenuItemRequest createMenuItemRequest) {
        menuItemService.createMenuItem(createMenuItemRequest);
    }

    @GetMapping("/{id}")
    public MenuItem getMenuItemById(@PathVariable String id) {
        return menuItemService.findById(id);
    }

    @PutMapping("/{id}")
    public IdResponse updateMenuItem(@PathVariable String id, @RequestBody MenuItem menuItem) {
        return menuItemService.updateMenuItem(id, menuItem);
    }

    @DeleteMapping("/{id}")
    public IdResponse deleteMenuItem(@PathVariable String id) {
        return menuItemService.deleteMenuItem(id);
    }
}
