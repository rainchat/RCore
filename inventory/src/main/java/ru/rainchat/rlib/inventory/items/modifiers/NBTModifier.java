package ru.rainchat.rlib.inventory.items.modifiers;


import ru.rainchat.rlib.messages.ChatUtil;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import ru.rainchat.rlib.utils.general.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NBTModifier implements ItemModifier {
    private String nbtData = "";

    public NBTModifier setNbtData(String nbtData) {
        this.nbtData = nbtData;
        return this;
    }

    @Override
    public String getName() {
        return "nbt";
    }

    @SuppressWarnings("deprecated")
    @Override
    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        if (MathUtil.isNullOrEmpty(nbtData)) {
            return original;
        }
        try {
            return Bukkit.getUnsafe().modifyItemStack(original, ChatUtil.translateRaw(nbtData, uuid, replacementSource));
        } catch (Throwable throwable) {
            return original;
        }
    }

    @Override
    public Object toObject() {
        return nbtData;
    }

    @Override
    public void loadFromObject(Object object) {
        this.nbtData = Objects.toString(object, "");
    }

    @Override
    public void loadFromItemStack(ItemStack itemStack) {
        // EMPTY
    }

    @Override
    public boolean canLoadFromItemStack(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return false;
    }

}