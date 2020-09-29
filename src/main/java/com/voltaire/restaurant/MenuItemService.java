package com.voltaire.restaurant;

import java.util.List;

public interface MenuItemService {

    MenuItem createMenuItem(MenuItemDTO menuItemDTO);

    List<MenuItem> findAll();

    MenuItem findById(Long id);

    boolean notExists(Long id);

    void updateMenuItem(Long id, MenuItem menuItem);

    void deleteMenuItem(Long id);
}
