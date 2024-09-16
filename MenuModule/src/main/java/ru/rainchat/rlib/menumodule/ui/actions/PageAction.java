package ru.rainchat.rlib.menumodule.ui.actions;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.utils.scheduler.RScheduler;

import java.util.Objects;
import java.util.UUID;

public class PageAction extends BaseAction {
    public PageAction(String string) {
        super(string);
    }

    @Override
    public void addToTask(UUID uuid, RScheduler scheduler, PaginationMenu menu) {

        String finalValue = getString();

        scheduler.run(() -> {
            if (Objects.equals(finalValue, "next")) getMenu().setPage(getMenu().getPaginationItems().getPage() + 1);
            if (Objects.equals(finalValue, "back")) getMenu().setPage(getMenu().getPaginationItems().getPage() - 1);
            if (Objects.equals(finalValue, "last")) getMenu().setPage(getMenu().getPaginationItems().getLastPage());
            if (Objects.equals(finalValue, "fist")) getMenu().setPage(getMenu().getPaginationItems().getFirstPage());
        });
    }
}