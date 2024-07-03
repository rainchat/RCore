package com.rainchat.rlib.inventory.menus;

import com.rainchat.rlib.inventory.items.ItemBuilder;
import com.rainchat.rlib.messages.placeholder.PlaceholderSupply;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LiteMenu implements InventoryHolder, Listener {

    private Inventory inventory;

    @Getter@Setter
    private int guiSize;
    @Setter@Getter
    private String guiName;
    private HashMap<Integer, BaseClickItem> clickableItems = new HashMap<>();
    private List<PlaceholderSupply<?>> globalPlaceholder;
    private HashMap<String,String> parameters;
    private BukkitTask update;

    public LiteMenu(Plugin plugin, String name, int size) {
        this.globalPlaceholder = new ArrayList<>();
        this.inventory = Bukkit.createInventory(this, size * 9, name);
        this.clickableItems = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        this.inventory = Bukkit.createInventory(this, guiSize * 9, guiName);
    }

    public List<PlaceholderSupply<?>> getPlaceholders() {
        return globalPlaceholder;
    }

    public void setPlaceholders(List<PlaceholderSupply<?>> globalPlaceholder) {
        this.globalPlaceholder = globalPlaceholder;
    }

    // Метод для добавления одной записи
    public void addParameter(String key, String value) {
        if (key != null && value != null) {
            parameters.put(key, value);
        }
    }

    // Метод для получения всего списка
    public HashMap<String, String> getParameters() {
        return new HashMap<>(parameters);
    }

    // Метод для добавления списка записей
    public void addParameters(Map<String, String> newParameters) {
        if (newParameters != null) {
            parameters.putAll(newParameters);
        }
    }

    public void open(Player player) {
        setupItems();
        player.openInventory(inventory);
    }


    public void close(Player player) {
        player.closeInventory();
    }

    protected void addItem(int slot, ClickItem clickItem) {
        clickableItems.put(slot, clickItem);
    }

    public void guiFill(ClickItem clickableItem) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (getItem(slot) != null) continue;
            this.setItem(slot, clickableItem);
        }
    }

    public HashMap<Integer, BaseClickItem> getItems() {
        return clickableItems;
    }

    public void setItem(int slot, BaseClickItem clickableItem) {
//        if (clickableItem.getItemStack() == null || clickableItem.getItemStack().getType().equals(Material.AIR)) {
//            this.clickableItems.put(slot, new ClickItem(new ItemBuilder().material(Material.AIR.toString()), inventoryClickEvent -> {}));
//            this.inventory.setItem(slot, clickableItem.getItemStack());
//            return;
//        }
        this.clickableItems.put(slot, clickableItem);
        //this.inventory.setItem(slot, clickableItem.getItemStack());
    }

    public void setItemLoad(int slot, BaseClickItem clickableItem) {
        if (clickableItem.getItemStack() == null || clickableItem.getItemStack().getType().equals(Material.AIR)) {
            this.clickableItems.put(slot, new ClickItem(new ItemBuilder().material(Material.AIR.toString()), inventoryClickEvent -> {}));
            this.inventory.setItem(slot, clickableItem.getItemStack());
            return;
        }
        this.clickableItems.put(slot, clickableItem);
        this.inventory.setItem(slot, clickableItem.getItemStack());
    }

    public void setupItems() {
        clickableItems.forEach((integer, baseClickItem) -> {
            this.inventory.setItem(integer, baseClickItem.getItemStack());
        });
    }

    public BaseClickItem getItem(int slot) {
        return this.clickableItems.getOrDefault(slot, null);
    }

    public void startUpdate(BukkitTask task) {
        if (update != null) {
            update.cancel();
        }
        this.update = task;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick().equals(ClickType.UNKNOWN) || event.getClickedInventory() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();


            if (event.getClickedInventory() == null) return;
            if (!player.getOpenInventory().getTopInventory().equals(getInventory())) return;

            if (event.getClickedInventory().equals(getInventory())) {

                BaseClickItem clickableItem = getItem(event.getSlot());
                if (clickableItem != null) {
                    event.setCancelled(true);
                    Consumer<InventoryClickEvent> clickEventConsumer = clickableItem.getInventoryClickEvent();
                    if (clickEventConsumer != null) {
                        clickEventConsumer.accept(event);
                    }
                }

            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(getInventory())) {
            if (update != null) {
                update.cancel();
            }
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
