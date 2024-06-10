package com.rainchat.rlib.menumodule.ui.placeholder;

import org.bukkit.entity.Player;

public class TargetPlaceholder extends SimplePlaceholder {


    public TargetPlaceholder(Player player) {
        super("target", player);
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
