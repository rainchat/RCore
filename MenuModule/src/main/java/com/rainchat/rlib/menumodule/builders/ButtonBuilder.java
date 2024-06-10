package com.rainchat.rlib.menumodule.builders;

import com.rainchat.rlib.menumodule.ui.buttons.SelectionButton;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.utils.builder.Builder;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Optional;

public class ButtonBuilder extends Builder<PaginationMenu, SelectionButton> {

    public static final ButtonBuilder INSTANCE = new ButtonBuilder();

    private ButtonBuilder() {
        registerDefaultButtons();
    }

    private void registerDefaultButtons() {
        register(s -> new SelectionButton(), "simple");
    }

    public SelectionButton getButton(PaginationMenu menu, String name, Map<String, Object> section) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
        SelectionButton button = Optional.ofNullable(keys.get("type"))
                .map(String::valueOf)
                .flatMap(string -> build(string, menu))
                .orElseGet(() -> build("simple", menu).orElse(null));
        if (button != null) {
            button.setInventory(menu);
            button.setFromSection(section);
        }
        return button;
    }
}
