package ru.rainchat.rlib.menumodule.ui.actions.requirements;

import org.bukkit.entity.Player;

public class PermissionsReq extends BaseRequirements {

    public PermissionsReq(String string) {
        super(string);
    }

    @Override
    public boolean getRequirement(String string, Player player) {
        return player.hasPermission(string);
    }

}