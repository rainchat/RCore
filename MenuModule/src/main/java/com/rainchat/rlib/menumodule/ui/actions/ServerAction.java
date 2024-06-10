package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ServerAction extends BaseAction {
    public ServerAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        scheduler.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getReplacedString(uuid)));
    }

}
