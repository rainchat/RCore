package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.menumodule.builders.MenuBuilder;
import com.rainchat.rlib.menumodule.ui.inventorys.SimpleMenu;
import com.rainchat.rlib.messages.placeholder.PlaceholderSupply;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenMenuAction extends BaseAction {
    public OpenMenuAction(String string) {
        super(string);
    }


    private String name;
    private HashMap<String, String> parameters = new HashMap<>();

    // Метод для добавления параметров из строки
    public void addParametersFromString(String paramStr) {
        if (paramStr != null && !paramStr.isEmpty()) {
            // Используем регулярное выражение для поиска параметров в квадратных скобках
            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(paramStr);

            while (matcher.find()) {
                String params = matcher.group(1);
                String[] pairs = params.split(" ");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
    }

    // Метод для извлечения имени из строки
    public String extractName(String paramStr) {
        if (paramStr != null && !paramStr.isEmpty()) {
            // Предполагаем, что имя находится до первой квадратной скобки
            int index = paramStr.indexOf('[');
            if (index != -1) {
                return paramStr.substring(0, index).trim();
            }
        }
        return null;
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {
        String string = getReplacedString(uuid);

        // Локальные переменные
        String name = null;
        HashMap<String, String> parameters = new HashMap<>();

        // Извлекаем имя из строки
        int index = string.indexOf('[');
        if (index != -1) {
            name = string.substring(0, index).trim();
        }

        // Используем регулярное выражение для поиска параметров в квадратных скобках
        Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(string);
        if (matcher.find()) {
            String[] pairs = matcher.group(1).split(" ");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }

        // Используем локальные переменные
        SimpleMenu simpleMenu = MenuBuilder.getInstance().getMenu(name);
        simpleMenu.addParameters(parameters);

        scheduler.run(() -> simpleMenu.open(Bukkit.getPlayer(uuid)));
    }


}
