package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.utils.scheduler.RScheduler;

import java.util.UUID;

public class UpdateAction extends BaseAction {

    public UpdateAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        scheduler.run(menu::updateInventory);
    }
}
