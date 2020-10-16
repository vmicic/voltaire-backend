package com.voltaire.restaurant;

import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem createMenuItem(@RequestBody MenuItemDto menuItemDto) {
        return menuItemService.createMenuItem(menuItemDto);
    }

    @GetMapping("/{id}")
    public MenuItem getMenuItemById(@PathVariable UUID id) {
        return menuItemService.findById(id);
    }

    @PutMapping("/{id}")
    public IdResponse updateMenuItem(@PathVariable UUID id, @RequestBody MenuItem menuItem) {
        return menuItemService.updateMenuItem(id, menuItem);
    }

    @DeleteMapping("/{id}")
    public IdResponse deleteMenuItem(@PathVariable UUID id) {
        return menuItemService.deleteMenuItem(id);
    }
}
