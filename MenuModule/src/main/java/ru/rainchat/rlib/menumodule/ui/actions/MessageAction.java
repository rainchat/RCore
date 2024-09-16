package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class MessageAction extends BaseAction {

    public MessageAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        String replacedString = getReplacedString(uuid);
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> scheduler.run(() -> player.sendMessage(replacedString)));
    }

}
