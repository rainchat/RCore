package com.rainchat.rlib.menumodule.ui.buttons;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.menus.BaseClickItem;
import com.rainchat.rlib.inventory.menus.PaginationMenu;

public interface MenuItem extends BaseClickItem {

    PaginationMenu getInventory();

    void setInventory(PaginationMenu inventory);

    void setItem(BaseItem baseItem);

    BaseItem getBaseItem();

}