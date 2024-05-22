package com.rainchat.rlib.inventory.pagination;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import org.bukkit.entity.Player;

public interface BasePagination {

    PaginationMenu getMenu();

    void setMenu(PaginationMenu menu);

    Player getPlayer();

    void setPlayer(Player player);

    void setupItems();

}
