package ru.rainchat.rlib.inventory.items;

import ru.rainchat.rlib.inventory.items.modifiers.ItemModifier;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BaseItem {
    private final List<ItemModifier> itemModifiers = new LinkedList<>();
    private List<PlaceholderSupply<?>> replacementSource = new ArrayList<>();
    private Material defaultMaterial = Material.STONE;

    public BaseItem addItemModifier(ItemModifier modifier) {
        itemModifiers.add(modifier);
        return this;
    }

    public BaseItem removeItemModifier(String name) {
        itemModifiers.removeIf(itemModifier -> itemModifier.getName().equals(name));
        return this;
    }

    public List<ItemModifier> getItemModifiers() {
        return Collections.unmodifiableList(itemModifiers);
    }

    public Map<String, Object> serializeItemModifiers() {
        Map<String, Object> map = new HashMap<>();
        itemModifiers.forEach(itemModifier -> map.put(itemModifier.getName(), itemModifier.toObject()));
        return map;
    }

    public List<PlaceholderSupply<?>> getStringReplacerMap() {
        List<PlaceholderSupply<?>> list = new ArrayList<>(replacementSource);
        return list;
    }

    public BaseItem setStringReplacer(List<PlaceholderSupply<?>> replacer) {
        this.replacementSource = replacer;
        return this;
    }

    public BaseItem addStringReplacer(PlaceholderSupply<?> replacer) {
        this.replacementSource.add(replacer);
        return this;
    }

    public ItemStack build(UUID uuid) {
        ItemStack itemStack = new ItemStack(defaultMaterial);
        for (ItemModifier modifier : itemModifiers) {
            itemStack = modifier.modify(itemStack, uuid, this);
        }
        return itemStack;
    }

    public ItemStack build(Player player) {
        return build(player.getUniqueId());
    }

    public ItemStack build() {
        return build(UUID.randomUUID());
    }

    public void setDefaultMaterial(Material material) {
        this.defaultMaterial = material;
    }

}