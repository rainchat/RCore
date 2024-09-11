package com.rainchat.rlib.menumodule.ui.inventorys;

import com.rainchat.rlib.menumodule.builders.ButtonBuilder;
import com.rainchat.rlib.menumodule.builders.PaginationBuilder;
import com.rainchat.rlib.menumodule.builders.PlaceholderBuilder;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.menumodule.ui.buttons.SelectionButton;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.bukkit.Bukkit.getName;

public class SimpleMenu extends PaginationMenu {

    public List<PlaceholderSupply<?>> placeholderSupplies;

    public SimpleMenu(Plugin plugin) {
        super(plugin, "null", 6);
        placeholderSupplies = new ArrayList<>();
    }


    public void setFromConfig(Map<String, Object> map) {

        map.forEach((key, value) -> {

            if (key.equalsIgnoreCase("menu-settings")) {

                if (!(value instanceof Map)) {
                    return;
                }
                Map<String, Object> settings = new CaseInsensitiveStringMap<>((Map<String, Object>) value);
                setupSettings(settings);
                init();

            } else if (key.equalsIgnoreCase("menu-items")) {

                if (!(value instanceof Map)) {
                    return;
                }
                Map<String, Object> items = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

                for (Map.Entry<String, Object> o : items.entrySet()) {
                    if (!(o.getValue() instanceof Map)) {
                        return;
                    }
                    Map<String, Object> item = new CaseInsensitiveStringMap<>((Map<String, Object>) o.getValue());
                    SelectionButton selectionButton = ButtonBuilder.INSTANCE.getButton(this, "menu_" + getName() + "_button_" + o.getKey(), item);
                    selectionButton.setInventory(this);
                    this.setItem(selectionButton.getSlot(), selectionButton);
                }

            } else if (key.equalsIgnoreCase("menu-pagination")) {
                if (!(value instanceof Map)) {
                    return;
                }
                Map<String, Object> pageItems = new CaseInsensitiveStringMap<>((Map<String, Object>) value);
                setPageItems(PaginationBuilder.INSTANCE.getPagination(this, pageItems));
            }

        });
    }

    private void setupSettings(Map<String, Object> settings) {

        Optional.ofNullable(settings.get("title")).ifPresent(o -> {
            this.setGuiName(String.valueOf(o));
        });

        Optional.ofNullable(settings.get("size")).ifPresent(o -> {
            this.setGuiSize(Integer.parseInt(String.valueOf(o)));
        });

    }

    @Override
    public void open(Player player) {
        setPlaceholders(PlaceholderBuilder.INSTANCE.getPlaceholders(this, player));

        getItems().values().forEach(baseClickItem -> baseClickItem.getBaseItem().setStringReplacer(getPlaceholders()));

        updateInventory();


        player.openInventory(getInventory());
    }

}
