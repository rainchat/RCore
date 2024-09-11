package com.rainchat.rlib.inventory.items.modifiers;

import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.general.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class HeadModifier implements ItemModifier {
    private String headBase64 = "1";

    public HeadModifier() {
    }

    public String getName() {
        return "head";
    }

    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        skullTextured(original);
        return original;
    }

    public Object toObject() {
        return this.headBase64;
    }

    public void loadFromObject(Object object) {
        this.headBase64 = String.valueOf(object);
    }

    public void loadFromItemStack(ItemStack itemStack) {
        this.headBase64 = String.valueOf(itemStack.getDurability());
    }

    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return MathUtil.getNumber(ChatUtil.translateRaw(this.headBase64, uuid, replacementSource)).map((bigDecimal) -> {
            return bigDecimal.shortValue() == itemStack.getDurability();
        }).orElse(false);
    }

    public HeadModifier setHead(String headBase64) {
        this.headBase64 = headBase64;
        return this;
    }

    public ItemStack skullTextured(ItemStack itemStack) {
        itemStack.setType(Material.PLAYER_HEAD);
        UUID id = UUID.nameUUIDFromBytes(headBase64.getBytes());
        int less = (int) id.getLeastSignificantBits();
        int most = (int) id.getMostSignificantBits();
        return Bukkit.getUnsafe().modifyItemStack(
                itemStack,
                "{SkullOwner:{Id:[I;" + (less * most) + "," + (less >> 23) + "," + (most / less) + "," + (most * 8731) + "],Properties:{textures:[{Value:\"" + headBase64 + "\"}]}}}"
        );
    }
}
