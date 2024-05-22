package com.rainchat.rlib.messages.title;

import org.bukkit.entity.Player;

public class TitleHolder {

    public static void sendTitle(Player player, String titleMessage, String subtitleMessage, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(titleMessage, subtitleMessage, fadeIn, stay, fadeOut);
    }

}
