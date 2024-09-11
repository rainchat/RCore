package com.rainchat.rlib.inventory.menus;

import com.rainchat.rlib.inventory.pagination.SimplePagination;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PaginationMenu extends LiteMenu {
    private SimplePagination paginationItems;

    public PaginationMenu(Plugin plugin, String name, int size) {
        super(plugin, name, size);
    }

    public SimplePagination getPaginationItems() {
        return this.paginationItems;
    }

    public void setPage(int page) {
        paginationItems.setPage(page);
    }

    @Override
    public void guiFill(ClickItem clickableItem) {
        for (int slot = 0; slot < getInventory().getSize(); slot++) {
            if (paginationItems.getItemSlots().contains(slot)) continue;
            if (getItem(slot) != null) continue;
            this.setItemLoad(slot, clickableItem);
        }
    }

    public void setPageItems(SimplePagination paginationItems) {
        this.paginationItems = paginationItems;
    }

    public boolean updateInventory() {

        Optional.ofNullable(paginationItems).ifPresent(SimplePagination::setupItems);

        setupPages();

        for (var entryMap : getItems().entrySet()) {
            this.setItemLoad(entryMap.getKey(), entryMap.getValue());
        }



        return true;
    }

    public void setupPages() {
        if (paginationItems == null) return;
        int clickableItemSize = paginationItems.getPaginationItems().size();
        int itemSlotSize = paginationItems.getItemSlots().size();

        int first = paginationItems.getPage() * itemSlotSize;
        int last = (paginationItems.getPage() + 1) * itemSlotSize;
        if (clickableItemSize <= first) {
            clearPages();
            return;
        }
        if (first < 0) {
            clearPages();
            return;
        }
        int m = 0;
        for (; first < last; first++) {
            BaseClickItem clickableItem = (clickableItemSize > first) ? paginationItems.getPaginationItems().get(first) : new EmptyItem();
            this.setItemLoad(paginationItems.getItemSlots().get(m), clickableItem);
            m++;
        }
    }

    public void clearPages() {
        paginationItems.getItemSlots().forEach(integer -> this.setItemLoad(integer, new EmptyItem()));
    }

    @Override
    public void open(Player player) {
        updateInventory();
        super.open(player);
    }


}
