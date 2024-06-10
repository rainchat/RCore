package com.rainchat.rlib.commands.services;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TabCompleterService {
    private final Map<String, BiFunction<CommandSender, Class<?>, List<String>>> completers = new HashMap<>();

    public TabCompleterService() {
        // Инициализация с лямбда-выражениями
        addCompleter("@player", (sender, args) -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        addCompleter("@world", (sender, args) -> Bukkit.getServer().getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList()));
        addCompleter("@material", (sender, args) -> Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(material -> material.name().toLowerCase())
                .collect(Collectors.toList()));
        addCompleter("@entity", (sender, args) -> Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::name)
                .collect(Collectors.toList()));
        addCompleter("@biome", (sender, args) -> Arrays.stream(Biome.values())
                .map(Biome::name)
                .collect(Collectors.toList()));
        addCompleter("@chatcolor", (sender, args) -> Arrays.stream(ChatColor.values())
                .map(ChatColor::name)
                .collect(Collectors.toList()));
        addCompleter("@sound", (sender, args) -> Arrays.stream(Sound.values())
                .map(Sound::name)
                .collect(Collectors.toList()));
        addCompleter("@enum", (sender, args) -> (args != null && args.isEnum())
                ? Arrays.stream(((Class<? extends Enum<?>>) args).getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList())
                : new ArrayList<>());
    }

    public void addCompleter(String parameter, BiFunction<CommandSender, Class<?>, List<String>> completer) {
        completers.put(parameter, completer);
    }

    public List<String> getCompletions(String parameter, CommandSender sender, Class<?> args) {
        BiFunction<CommandSender, Class<?>, List<String>> completer = completers.get(parameter);
        if (completer != null) {
            List<String> list = completer.apply(sender, args);
            if (list.isEmpty()) return new ArrayList<>();
            return list;
        }
        return new ArrayList<>();
    }
}
