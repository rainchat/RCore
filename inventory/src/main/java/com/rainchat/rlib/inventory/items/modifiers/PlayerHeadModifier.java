package com.rainchat.rlib.inventory.items.modifiers;

import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.PlaceholderSupply;
import com.rainchat.rlib.utils.general.MathUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class PlayerHeadModifier implements ItemModifier {
    private String playerOwner = "1";

    public PlayerHeadModifier() {
    }

    public String getName() {
        return "head";
    }

    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        original.setType(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) original.getItemMeta();
        skull.setOwner(playerOwner);
        original.setItemMeta(skull);
        return original;
    }

    public Object toObject() {
        return this.playerOwner;
    }

    public void loadFromObject(Object object) {
        this.playerOwner = String.valueOf(object);
    }

    public void loadFromItemStack(ItemStack itemStack) {
        this.playerOwner = String.valueOf(itemStack.getDurability());
    }

    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return MathUtil.getNumber(ChatUtil.translateRaw(this.playerOwner, uuid, replacementSource)).map((bigDecimal) -> {
            return bigDecimal.shortValue() == itemStack.getDurability();
        }).orElse(false);
    }

    public PlayerHeadModifier setHead(String playerOwner) {
        this.playerOwner = playerOwner;
        return this;
    }

}
