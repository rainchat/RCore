package ru.rainchat.rlib.menumodule.ui.buttons;

import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.inventory.menus.BaseClickItem;
import ru.rainchat.rlib.inventory.menus.PaginationMenu;

public interface MenuItem extends BaseClickItem {

    PaginationMenu getInventory();

    void setInventory(PaginationMenu inventory);

    void setItem(BaseItem baseItem);

    BaseItem getBaseItem();

}