package com.rainchat.rlib.menumodule.ui.buttons;

import com.rainchat.rlib.menumodule.builders.ActionBuilder;
import com.rainchat.rlib.menumodule.builders.PlaceholderBuilder;
import com.rainchat.rlib.menumodule.ui.actions.Action;
import com.rainchat.rlib.inventory.items.ItemModifierBuilder;
import com.rainchat.rlib.utils.RUtility;
import com.rainchat.rlib.utils.collections.CaseInsensitiveStringMap;
import com.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.entity.Player;

import java.util.*;

public class SelectionButton extends SimpleItem implements Cloneable {

    private List<Action> actions = new ArrayList<>();
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
        getInventory().setItem(slot, this);
    }

    private void setAction(Object o) {


        if (o instanceof Map) {
            Map<String, Object> keys = new CaseInsensitiveStringMap<>((Map<String, Object>) o);
            actions = Optional.ofNullable(keys.get("left")).map(value ->
                    ActionBuilder.INSTANCE.getActions(getInventory(), getBaseItem(), value)).orElse(Collections.emptyList());
        }
        setInventoryClickEvent(baseClick -> {
            RScheduler scheduler = RUtility.syncScheduler();

            actions.forEach(action -> {
                action.setReplacedString(PlaceholderBuilder.INSTANCE.getPlaceholders(getInventory(), (Player) baseClick.getWhoClicked()));
                action.addToTask(baseClick.getWhoClicked().getUniqueId(), scheduler, getInventory());
            });
        });
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
