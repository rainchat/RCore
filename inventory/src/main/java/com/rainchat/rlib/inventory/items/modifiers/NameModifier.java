package com.rainchat.rlib.inventory.items.modifiers;

import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class NameModifier extends ItemMetaModifier {
    private String name;

    public NameModifier() {
    }

    public String getName() {
        return "name";
    }

    public NameModifier setName(String name) {
        this.name = name;
        return this;
    }

    public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        meta.setDisplayName(ChatUtil.translateRaw(this.name, uuid, replacementSource));
        return meta;
    }

    public void loadFromItemMeta(ItemMeta meta) {
        this.name = meta.getDisplayName();
    }

    public boolean canLoadFromItemMeta(ItemMeta meta) {
        return meta.hasDisplayName();
    }

    public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        String replaced = ChatUtil.translateRaw(this.name, uuid, replacementSource);
        return meta.hasDisplayName() || replaced.equals(meta.getDisplayName());
    }

    public Object toObject() {
        return this.name;
    }

    public void loadFromObject(Object object) {
        this.name = String.valueOf(object);
    }
}