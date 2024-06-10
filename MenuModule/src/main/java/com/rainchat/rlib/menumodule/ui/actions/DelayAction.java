package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.general.MathUtil;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Optional;
import java.util.UUID;

public class DelayAction extends BaseAction {
    public DelayAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {

        String finalValue = getReplacedString(uuid);
        if (!MathUtil.isInteger(finalValue)) {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Invalid delay: " + finalValue));
            return;
        }

        scheduler.addAfter(Integer.parseInt(finalValue)).run(() -> {
        });
    }
}
