package ru.rainchat.rlib.menumodule.builders;

import ru.rainchat.rlib.menumodule.ui.inventorys.SimpleMenu;
import ru.rainchat.rlib.menumodule.ui.pagination.OnlinePlayersPagination;
import ru.rainchat.rlib.menumodule.ui.pagination.TestPagination;
import ru.rainchat.rlib.utils.builder.Builder;
import ru.rainchat.rlib.utils.injection.Injector;

import java.util.Map;

public class PaginationBuilder extends Builder<String, OnlinePlayersPagination> {

    public static final PaginationBuilder INSTANCE = new PaginationBuilder();
    private final Injector injector;

    private PaginationBuilder() {
        this.injector = new Injector();
        registerDefaultActions();
    }

    private void registerDefaultActions() {
        register(s -> {
            OnlinePlayersPagination pagination = new OnlinePlayersPagination();
            injector.injectDependencies(pagination);
            return pagination;
        }, "online-players", "online");

        register(s -> {
            TestPagination pagination = new TestPagination();
            injector.injectDependencies(pagination);
            return pagination;
        }, "test");
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

    public Injector getInjector() {
        return injector;
    }
}
