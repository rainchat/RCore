package com.rainchat.rlib.inventory.menus;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.items.ItemBuilder;
import com.rainchat.rlib.inventory.menus.BaseClickItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class EmptyItem implements BaseClickItem {

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public BaseItem getBaseItem() {
        return new ItemBuilder().material("AIR");
    }

    @Override
    public Consumer<InventoryClickEvent> getInventoryClickEvent() {
        return inventoryClickEvent -> {
        };
    }
}
