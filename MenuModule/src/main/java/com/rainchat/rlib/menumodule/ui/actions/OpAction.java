package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class OpAction extends CommandAction {

    public OpAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        String replacedString = getFinalCommand(getReplacedString(uuid));
        Optional.ofNullable(Bukkit.getPlayer(uuid))
                .ifPresent(player -> {
                    if (player.isOp()) {
                        scheduler.run(() -> player.chat(replacedString));
                    } else {
                        scheduler.run(() -> {
                            try {
                                player.setOp(true);
                                player.chat(replacedString);
                            } finally {
                                player.setOp(false);
                            }
                        });
                    }
                });
    }

}
