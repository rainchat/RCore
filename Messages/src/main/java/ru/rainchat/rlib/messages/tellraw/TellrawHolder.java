package ru.rainchat.rlib.messages.tellraw;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TellrawHolder {

    public static void tellraw(Player player, String json) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + json);
    }

}
