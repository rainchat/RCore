package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class PlayerAction extends CommandAction {

    public PlayerAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        String command = getFinalCommand(getReplacedString(uuid));
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> scheduler.run(() -> player.chat(command)));
    }

}
