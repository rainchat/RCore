package ru.rainchat.rlib.inventory.items.modifiers;

import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface ItemModifier {
    String getName();

    ItemStack modify(ItemStack var1, UUID var2, List<PlaceholderSupply<?>> replacementSource);

    Object toObject();

    void loadFromObject(Object var1);

    void loadFromItemStack(ItemStack var1);

    default boolean canLoadFromItemStack(ItemStack itemStack) {
        return true;
    }

    boolean compareWithItemStack(ItemStack var1, UUID var2, List<PlaceholderSupply<?>> replacementSource);

    default boolean compareWithItemStack(ItemStack itemStack, UUID uuid) {
        return this.compareWithItemStack(itemStack, uuid, Collections.emptyList());
    }

    default boolean compareWithItemStack(ItemStack itemStack) {
        return this.compareWithItemStack(itemStack, UUID.randomUUID(), Collections.emptyList());
    }

    default ItemStack modify(ItemStack original, UUID uuid, BaseItem baseItem) {
        List<PlaceholderSupply<?>> placeholders = baseItem.getStringReplacerMap();
        return this.modify(original, uuid, baseItem.getStringReplacerMap());
    }
}