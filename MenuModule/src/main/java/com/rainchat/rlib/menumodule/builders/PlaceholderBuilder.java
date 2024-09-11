package com.rainchat.rlib.menumodule.builders;

import com.rainchat.rlib.menumodule.ui.placeholder.PlayerReplacements;
import com.rainchat.rlib.menumodule.ui.placeholder.SimplePlaceholder;
import com.rainchat.rlib.inventory.menus.PaginationMenu;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.builder.Builder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderBuilder extends Builder<Player, PlaceholderSupply<?>> {

    public static final PlaceholderBuilder INSTANCE = new PlaceholderBuilder();

    private PlaceholderBuilder() {
        registerDefaultActions();
    }

    private void registerDefaultActions() {
        register(PlayerReplacements::new, "player");
    }

    public List<PlaceholderSupply<?>> getPlaceholders(PaginationMenu menu, Player player) {
        List<PlaceholderSupply<?>> placeholders = new ArrayList<>();
        getFunctionMap().forEach((s, holders) -> {
            PlaceholderSupply<?> placeholder = build(s, player).orElseGet(() -> new PlayerReplacements(player));
            if (placeholder instanceof SimplePlaceholder) {
                ((SimplePlaceholder) placeholder).setMenu(menu);
            }
            placeholders.add(placeholder);
        });
        return placeholders;
    }


}
