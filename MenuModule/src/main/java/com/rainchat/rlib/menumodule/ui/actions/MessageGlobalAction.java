package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.UUID;

public class MessageGlobalAction extends BaseAction {

    public MessageGlobalAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        String replacedString = getReplacedString(uuid);
        Bukkit.getOnlinePlayers().forEach(player -> scheduler.run(() -> player.sendMessage(replacedString)));
    }
}