package ru.rainchat.rlib.inventory.pagination;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import org.bukkit.entity.Player;

public interface BasePagination {

    PaginationMenu getMenu();

    void setMenu(PaginationMenu menu);

    Player getPlayer();

    void setPlayer(Player player);

    void setupItems();

}
