package ru.rainchat.rlib.inventory.items.modifiers;

import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public abstract class ItemMetaModifier implements ItemModifier {
    public ItemMetaModifier() {
    }

    public abstract ItemMeta modifyMeta(ItemMeta var1, UUID var2, List<PlaceholderSupply<?>> replacementSource);

    public abstract void loadFromItemMeta(ItemMeta var1);

    public abstract boolean canLoadFromItemMeta(ItemMeta var1);

    public abstract boolean compareWithItemMeta(ItemMeta var1, UUID var2, List<PlaceholderSupply<?>> replacementSource);

    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        ItemMeta itemMeta = original.getItemMeta();
        if (itemMeta != null) {
            original.setItemMeta(this.modifyMeta(itemMeta, uuid, replacementSource));
        }

        return original;
    }

    public void loadFromItemStack(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            this.loadFromItemMeta(itemStack.getItemMeta());
        }

    }

    public boolean canLoadFromItemStack(ItemStack itemStack) {
        return itemStack.hasItemMeta() && this.canLoadFromItemMeta(itemStack.getItemMeta());
    }

    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return itemStack.hasItemMeta() && this.compareWithItemMeta(itemStack.getItemMeta(), uuid, replacementSource);
    }
}