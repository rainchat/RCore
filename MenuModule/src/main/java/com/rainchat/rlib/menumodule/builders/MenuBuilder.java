package com.rainchat.rlib.menumodule.builders;

import com.rainchat.rlib.menumodule.ui.inventorys.SimpleMenu;
import com.rainchat.rlib.inventory.menus.LiteMenu;

import com.rainchat.rlib.utils.builder.Builder;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;
import com.rainchat.rlib.utils.config.Config;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuBuilder extends Builder<Plugin, SimpleMenu> {

    public HashMap<String, Map<String, Object>> menus = new HashMap<>();
    private final Plugin plugin;

    public MenuBuilder(Plugin plugin) {
        this.plugin = plugin;
        registerDefaultMenus();
    }

    private void registerDefaultMenus() {
        register(SimpleMenu::new, "simple");
    }

    public void registerMenu(String name, Config config) {
        this.menus.put(name, config.getNormalizedValues(false));
    }

    public LiteMenu getMenu(String name) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(menus.get(name));
        SimpleMenu menu = Optional.ofNullable(keys.get("menu-settings.menu-type"))
                .map(String::valueOf)
                .flatMap(string -> build(string, plugin))
                .orElseGet(() -> build("simple", plugin).orElse(null));

        if (menu != null) {
            menu.setFromConfig(menus.get(name));
        }

        return menu;
    }

}
