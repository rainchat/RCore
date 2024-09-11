package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.scheduler.RScheduler;

import java.util.List;
import java.util.UUID;

public interface Action {

    PaginationMenu getMenu();

    void setMenu(PaginationMenu menu);

    BaseItem getItem();

    void setItem(BaseItem baseItem);

    void setReplacedString(List<PlaceholderSupply<?>> placeholders);

    void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu);
}
