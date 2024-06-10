package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;

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
