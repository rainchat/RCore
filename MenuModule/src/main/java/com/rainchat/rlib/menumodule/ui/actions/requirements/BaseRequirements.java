package com.rainchat.rlib.menumodule.ui.actions.requirements;

import com.rainchat.rlib.menumodule.ui.actions.BaseAction;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseRequirements extends BaseAction {

    protected BaseRequirements(String string) {
        super(string);
    }

    private static String findParameterText(String input, String parameter) {
        String parameterRegex = parameter + "\\s(.*?)(?=\\s-\\w|$)";
        Pattern pattern = Pattern.compile(parameterRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+-\\s+", " ").trim();
        }
        return "";
    }

    private static String removeParameters(String input) {
        String parametersRegex = "\\s-\\w+\\s(.*?)(?=\\s-\\w|$)";
        return input.replaceAll(parametersRegex, "").replaceAll("\\s+", " ").trim();
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        // Ищем текст после параметров -e и -s
        String eText = findParameterText(getReplacedString(uuid), "-e");
        String sText = findParameterText(getReplacedString(uuid), "-s");
        String textWithoutParameters = removeParameters(getReplacedString(uuid));

        Player player = Bukkit.getPlayer(uuid);
        if (getRequirement(textWithoutParameters, player)) {
            if (!sText.isEmpty()) scheduler.run(() -> player.sendMessage(sText));
        } else {
            if (!eText.isEmpty()) scheduler.run(() -> player.sendMessage(eText));
            scheduler.setCancel(true);
        }
    }

    public abstract boolean getRequirement(String string, Player player);

}
