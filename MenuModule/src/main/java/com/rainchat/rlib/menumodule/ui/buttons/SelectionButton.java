package com.rainchat.rlib.menumodule.ui.buttons;

import com.rainchat.rlib.menumodule.builders.ActionBuilder;
import com.rainchat.rlib.menumodule.builders.PlaceholderBuilder;
import com.rainchat.rlib.menumodule.ui.actions.Action;
import com.rainchat.rlib.inventory.items.ItemModifierBuilder;
import com.rainchat.rlib.utils.RUtility;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SelectionButton extends SimpleItem implements Cloneable {

    private Map<String, List<Action>> actionsMap = new HashMap<>();
    @Getter
    private int slot;

    public void setFromSection(Map<String, Object> section) {

        Optional.ofNullable(section.get("slot")).ifPresent(o -> {
            slot = (Integer.parseInt(String.valueOf(o)));
        });

        if (section.get("item") instanceof Map) {
            ItemModifierBuilder.INSTANCE
                    .getItemModifiers(new CaseInsensitiveStringMap<>((Map<String, Object>) section.get("item")))
                    .forEach(getBaseItem()::addItemModifier);
        }

        setAction(section.get("actions"));
        //getInventory().setItem(slot, this);
    }

    private void setAction(Object o) {
        if (o instanceof Map) {
            Map<String, Object> keys = new CaseInsensitiveStringMap<>((Map<String, Object>) o);
            for (ClickType clickType : ClickType.values()) {
                actionsMap.put(clickType.name().toLowerCase(), Optional.ofNullable(keys.get(clickType.name().toLowerCase()))
                        .map(value -> ActionBuilder.INSTANCE.getActions(getInventory(), getBaseItem(), value))
                        .orElse(Collections.emptyList()));
            }
            // Устанавливаем действия по умолчанию, если они есть в корне "actions"
            actionsMap.put("default", Optional.ofNullable(keys.get("default"))
                    .map(value -> ActionBuilder.INSTANCE.getActions(getInventory(), getBaseItem(), value))
                    .orElse(Collections.emptyList()));
        } else {
            actionsMap.put("default", ActionBuilder.INSTANCE.getActions(getInventory(), getBaseItem(), o));
        }

        setInventoryClickEvent(baseClick -> {
            RScheduler scheduler = RUtility.syncScheduler();
            ClickType clickType = baseClick.getClick();

            List<Action> actions = actionsMap.getOrDefault(clickType.name().toLowerCase(), actionsMap.get("default"));
            actions.forEach(action -> {
                action.setReplacedString(PlaceholderBuilder.INSTANCE.getPlaceholders(getInventory(), (Player) baseClick.getWhoClicked()));
                action.addToTask(baseClick.getWhoClicked().getUniqueId(), scheduler, getInventory());
            });
        });
    }

    @Override
    public ItemStack getItemStack() {
        actionsMap.values().forEach(actions -> actions.forEach(action -> action.setItem(getBaseItem())));
        return super.getItemStack();
    }

    @Override
    public SelectionButton clone() {
        try {
            return (SelectionButton) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
