package com.rainchat.rlib.menumodule.ui.actions.requirements;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class LevelReq extends BaseRequirements {

    public LevelReq(String string) {
        super(string);
    }

    @Override
    public boolean getRequirement(String string, Player player) {
        return player.getLevel() >= Integer.parseInt(string)
                || player.getGameMode().equals(GameMode.CREATIVE);
    }

}
