package com.rainchat.rlib.menumodule.ui.actions;

import com.rainchat.rlib.inventory.items.BaseItem;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.PlaceholderSupply;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseAction implements Action, Cloneable {

    private final String string;

    private List<PlaceholderSupply<?>> placeholders = new ArrayList<>();

    private PaginationMenu menu;

    private BaseItem Item;

    protected BaseAction(String string) {
        this.string = string;
    }

    /**
     * Get the replaced string
     *
     * @param uuid the unique id
     * @return the replaced string
     */
    protected String getReplacedString(UUID uuid) {
        return ChatUtil.translateRaw(string, uuid, getItem().getStringReplacerMap());
    }

    protected String getString() {
        return string;
    }

    public void setReplacedString(List<PlaceholderSupply<?>> placeholders) {
        this.placeholders = placeholders;
    }

    public PaginationMenu getMenu() {
        return menu;
    }

    public void setMenu(PaginationMenu menu) {
        this.menu = menu;
    }

    public BaseItem getItem() {
        return Item;
    }

    public void setItem(BaseItem item) {
        Item = item;
    }

}
