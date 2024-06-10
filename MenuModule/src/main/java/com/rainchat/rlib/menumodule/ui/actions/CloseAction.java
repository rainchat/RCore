package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CloseAction extends BaseAction {

    public CloseAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        Player player = Bukkit.getPlayer(uuid);
        if (getMenu() == null || player == null) {
            return;
        }
        scheduler.run(() -> getMenu().close(player));
    }

}
