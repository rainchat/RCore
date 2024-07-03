package com.rainchat.rlib.menumodule.ui.pagination;

import com.google.gson.internal.LinkedTreeMap;
import com.rainchat.rlib.menumodule.builders.ButtonBuilder;
import com.rainchat.rlib.menumodule.ui.buttons.SelectionButton;
import com.rainchat.rlib.menumodule.ui.buttons.SimpleItem;
import com.rainchat.rlib.menumodule.ui.inventorys.SimpleMenu;
import com.rainchat.rlib.menumodule.ui.placeholder.TargetPlaceholder;
import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.items.modifiers.PlayerHeadModifier;
import com.rainchat.rlib.inventory.menus.BaseClickItem;
import com.rainchat.rlib.inventory.pagination.SimplePagination;
import com.rainchat.rlib.messages.placeholder.PlaceholderSupply;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class OnlinePlayersPagination extends SimplePagination {

    public Map<String, Object> sectionItem = new HashMap<>();
    private final List<BaseClickItem> clickableItems = new ArrayList<>();

    public void setFromSection(Map<String, Object> section) {
        this.sectionItem = section;
        Optional.ofNullable(section.get("slots")).ifPresent((o) -> {
            List<Integer> slots = parsePagination(String.valueOf(o));

            this.setItemSlots(slots);
        });
    }

    @Override
    public void setupItems() {
        this.clickableItems.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            SelectionButton clickItem = getPageItem();

            clickItem.setInventory(getMenu());

            List<PlaceholderSupply<?>> supplies = getPlaceholders();
            supplies.add(new TargetPlaceholder(player));

            clickItem.getBaseItem().setStringReplacer(supplies);

            this.clickableItems.add(clickItem);
        }

        this.setItems(this.clickableItems);
    }

    public List<Integer> parsePagination(String pagination) {
        List<Integer> numbers = new ArrayList<>();
        String[] parts = pagination.split(",");

        for (String part : parts) {
            if (part.contains("-")) {
                String[] bounds = part.split("-");
                int start = Integer.parseInt(bounds[0]);
                int end = Integer.parseInt(bounds[1]);

                for (int i = start; i <= end; i++) {
                    numbers.add(i);
                }
            } else {
                numbers.add(Integer.parseInt(part));
            }
        }

        return numbers;
    }

    public List<PlaceholderSupply<?>> getPlaceholders() {
        List<PlaceholderSupply<?>> placeholderSupplies = new ArrayList<>();
        getMenu().getPlaceholders().forEach(placeholder -> placeholderSupplies.add(placeholder));
        return placeholderSupplies;
    }

    public SelectionButton getPageItem() {
        AtomicReference<SelectionButton> pageItemRef = new AtomicReference<>();

        Optional.ofNullable(sectionItem.get("page")).ifPresent((o) -> {
            if (o instanceof Map) {
                Map<String, Object> item = new CaseInsensitiveStringMap((Map<String, Object>) o);
                pageItemRef.set(ButtonBuilder.INSTANCE.getButton(this.getMenu(), "menu_" + Bukkit.getName() + "_button_fill_item", item));
            }
        });

        return pageItemRef.get();
    }
}
