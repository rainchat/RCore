package ru.rainchat.rlib.menumodule.ui.placeholder;

import org.bukkit.entity.Player;

public class PlayerReplacements extends SimplePlaceholder {

    public PlayerReplacements(Player player) {
        super("player", player);
    }

    @Override
    public String getReplacement(String base, String fullKey) {

        switch (base) {
            case "name":
                return player.getName();

            case "world":
                if (player != null) {
                    return player.getWorld().getName();
                }

                return "";

            case "uuid":
                return player.getUniqueId().toString();

        }

        return "";
    }


}
