package ru.rainchat.rlib.inventory.items.modifiers;

import ru.rainchat.rlib.messages.ChatUtil;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MaterialModifier implements ItemModifier {
    private String materialString;

    @Override
    public String getName() {
        return "material";
    }

    @Override
    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        Optional
                .of(Material.valueOf(ChatUtil.translateRaw(materialString, uuid, replacementSource).toUpperCase()))
                .ifPresent(original::setType);
        return original;
    }

    @Override
    public Object toObject() {
        return this.materialString;
    }

    @Override
    public void loadFromObject(Object object) {this.materialString = String.valueOf(object);}

    @Override
    public void loadFromItemStack(ItemStack itemStack) {
        this.materialString = itemStack.getType().name();
    }

    @Override
    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return itemStack.getType().name().equalsIgnoreCase(ChatUtil.translateRaw(materialString, uuid, replacementSource));
    }

    /**
     * Set the material
     *
     * @param material the material
     * @return {@code this} for builder chain
     */
    public MaterialModifier setMaterial(Material material) {
        this.materialString = material.name();
        return this;
    }

    /**
     * Set the material
     *
     * @param material the material
     * @return {@code this} for builder chain
     */
    public MaterialModifier setMaterial(String material) {
        this.materialString = material;
        return this;
    }
}