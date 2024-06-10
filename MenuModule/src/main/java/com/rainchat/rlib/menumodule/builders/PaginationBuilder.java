package com.rainchat.rlib.menumodule.builders;


import com.rainchat.rlib.menumodule.ui.inventorys.SimpleMenu;
import com.rainchat.rlib.menumodule.ui.pagination.OnlinePlayersPagination;
import com.rainchat.rlib.menumodule.ui.pagination.TestPagination;
import com.rainchat.rlib.utils.builder.Builder;

import java.util.Map;

public class
PaginationBuilder extends Builder<String, OnlinePlayersPagination> {

    public static final PaginationBuilder INSTANCE = new PaginationBuilder();

    private PaginationBuilder() {
        registerDefaultActions();
    }

    private void registerDefaultActions() {
        register(s -> new OnlinePlayersPagination(), "online-players", "online");
        register(s -> new TestPagination(), "test");

    }


    public OnlinePlayersPagination getPagination(SimpleMenu inventory, Map<String, Object> keys) {
        String menu = (String) keys.getOrDefault("page-type", "online");
        OnlinePlayersPagination pagination = build(menu, "").orElse(null);
        if (pagination != null) {
            pagination.setMenu(inventory);
            pagination.setFromSection(keys);
        }
        return pagination;
    }
}