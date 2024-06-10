package com.rainchat.rlib.menumodule.ui.actions.requirements;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeReq extends BaseRequirements {

    public GamemodeReq(String string) {
        super(string);
    }

    @Override
    boolean getRequirement(String string, Player player) {
        return player.getGameMode().equals(GameMode.valueOf(string));
    }

}
