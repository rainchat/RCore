package com.rainchat.rlib.inventory.menus;

import com.rainchat.rlib.inventory.items.BaseItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface BaseClickItem {


    public ItemStack getItemStack();

    public BaseItem getBaseItem();

    public Consumer<InventoryClickEvent> getInventoryClickEvent();

}
