package ru.rainchat.rlib.messages.placeholder.replacers;

import ru.rainchat.rlib.messages.placeholder.base.CustomPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TargetReplacements extends CustomPlaceholder<Player> {
    private final OfflinePlayer player;

    public TargetReplacements(OfflinePlayer player) {
        super("target_");
        this.player = player;
    }

    public TargetReplacements(UUID playerUUID) {
        super("target_");
        this.player = Bukkit.getOfflinePlayer(playerUUID);
    }

    public Class<Player> forClass() {
        return Player.class;
    }

    public String getReplacement(String base, String fullKey) {
        return switch (base) {
            case "name" -> this.player.getName();
            case "world" -> {
                if (this.player instanceof Player) {
                    yield ((Player) this.player).getWorld().getName();
                }
                yield "";
            }
            case "uuid" -> this.player.getUniqueId().toString();
            default -> "";
        };
    }

}