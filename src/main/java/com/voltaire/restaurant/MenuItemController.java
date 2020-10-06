package com.voltaire.restaurant;

import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.MenuItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemService.createMenuItem(menuItemDto);

        return new ResponseEntity<>(menuItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        MenuItem menuItem = menuItemService.findById(id);

        return new ResponseEntity<>(menuItem, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdResponse> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        var idResponse = menuItemService.updateMenuItem(id, menuItem);

        return new ResponseEntity<>(idResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IdResponse> deleteMenuItem(@PathVariable Long id) {
        var idResponse = menuItemService.deleteMenuItem(id);

        return new ResponseEntity<>(idResponse, HttpStatus.OK);
    }
}
