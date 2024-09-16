package ru.rainchat.rlib.inventory.menus;

import ru.rainchat.rlib.inventory.items.BaseItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickItem implements BaseClickItem {

    private final BaseItem item;
    private Consumer<InventoryClickEvent> inventoryClickEvent;

    public ClickItem(BaseItem item, Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.item = item;
        this.inventoryClickEvent = inventoryClickEvent;
    }

    public static ClickItem empty(BaseItem item) {
        return new ClickItem(item, inventoryClickEvent1 -> {});
    }

    public void setInventoryClickEvent(Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.inventoryClickEvent = inventoryClickEvent;
    }

    public BaseItem getBaseItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return item.build();
    }

    public Consumer<InventoryClickEvent> getInventoryClickEvent() {
        return inventoryClickEvent;
    }
}
