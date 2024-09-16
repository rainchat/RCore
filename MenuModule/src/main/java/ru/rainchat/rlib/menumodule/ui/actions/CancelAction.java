package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.utils.scheduler.RScheduler;

import java.util.UUID;

public class CancelAction extends BaseAction {

    public CancelAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        scheduler.setCancel(true);
    }
}
