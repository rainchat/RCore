package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import ru.rainchat.rlib.utils.scheduler.RScheduler;

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
