package ru.rainchat.rlib.menumodule.ui.buttons;

import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class SimpleItem implements MenuItem {
    private PaginationMenu menu;
    private Consumer<InventoryClickEvent> inventoryClickEvent;

    private BaseItem baseItem = new BaseItem();

    @Override
    public PaginationMenu getInventory() {
        return menu;
    }

    @Override
    public void setInventory(PaginationMenu inventory) {
        this.menu = inventory;
    }

    public void setItem(BaseItem baseItem) {
        this.baseItem = baseItem;
    }

    public ItemStack getItemStack() {
        return baseItem.build();
    }

    public BaseItem getBaseItem() {
        return baseItem;
    }

    public Consumer<InventoryClickEvent> getInventoryClickEvent() {
        return inventoryClickEvent;
    }

    public void setInventoryClickEvent(Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.inventoryClickEvent = inventoryClickEvent;
    }

}
