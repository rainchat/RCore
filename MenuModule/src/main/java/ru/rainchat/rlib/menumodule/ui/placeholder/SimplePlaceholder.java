package ru.rainchat.rlib.menumodule.ui.placeholder;

import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.messages.placeholder.base.CustomPlaceholder;
import org.bukkit.entity.Player;

public abstract class SimplePlaceholder extends CustomPlaceholder<Player> {

    public final Player player;
    private PaginationMenu paginationMenu;

    public SimplePlaceholder(String prefix, Player player) {
        super(prefix);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public PaginationMenu getMenu() {
        return paginationMenu;
    }

    public void setMenu(PaginationMenu paginationMenu) {
        this.paginationMenu = paginationMenu;
    }

}
