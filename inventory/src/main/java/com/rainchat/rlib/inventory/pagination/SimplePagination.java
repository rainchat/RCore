package com.rainchat.rlib.inventory.pagination;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.menus.BaseClickItem;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SimplePagination implements BasePagination {

    private PaginationMenu simpleMenu;
    private Player player;

    private int page;
    private List<Integer> itemSlots;
    private List<BaseClickItem> paginationItems;

    @Override
    public PaginationMenu getMenu() {
        return simpleMenu;
    }

    @Override
    public void setMenu(PaginationMenu menu) {
        this.simpleMenu = menu;
    }

    abstract public void setupItems();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        if (this.paginationItems.isEmpty()) return;
        if (this.itemSlots.isEmpty()) return;
        if (page > getLastPage() || page < getFirstPage()) return;
        int oldPage = this.page;
        this.page = page;

        if (!getMenu().updateInventory()) this.page = oldPage;
    }

    public int getFirstPage() {
        return 0;
    }

    public List<BaseClickItem> getPaginationItems() {
        return this.paginationItems;
    }

    public List<Integer> getItemSlots() {
        return this.itemSlots;
    }

    public void setItemSlots(List<Integer> ints) {
        this.itemSlots = ints;
    }

    public int getLastPage() {
        int m = (int) Math.ceil((double) getPaginationItems().size() / getItemSlots().size()) - 1;
        return m != -1 ? m : 0;
    }

    public void setItems(List<BaseClickItem> clickableItems) {
        this.paginationItems = clickableItems;
    }

    public boolean isLastPage() {
        return this.page == this.getLastPage();
    }

    public boolean isFirstPage() {
        return this.page == 0;
    }

}