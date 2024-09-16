package ru.rainchat.rlib.menumodule.ui.actions.requirements;

import ru.rainchat.rlib.utils.hooks.EconomyBridge;
import org.bukkit.entity.Player;

public class EcoReq extends BaseRequirements {

    public EcoReq(String string) {
        super(string);
    }

    @Override
    public boolean getRequirement(String string, Player player) {
        if (player == null) {
            return true;
        }
        return hasMoney(player, string);
    }

    private boolean hasMoney(Player player, String text) {
        return EconomyBridge.hasMoney(player, Double.parseDouble(text));
    }

}
