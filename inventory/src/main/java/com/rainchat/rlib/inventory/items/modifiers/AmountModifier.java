package com.rainchat.rlib.inventory.items.modifiers;

import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.general.MathUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class AmountModifier implements ItemModifier {
    private String amount = "1";

    public AmountModifier() {
    }

    public String getName() {
        return "amount";
    }

    public ItemStack modify(ItemStack original, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        original.setAmount(MathUtil.getNumber(ChatUtil.translateRaw(this.amount, uuid, replacementSource)).get().intValue());
        return original;
    }

    public Object toObject() {
        return this.amount;
    }

    public void loadFromObject(Object object) {
        this.amount = String.valueOf(object);
    }

    public void loadFromItemStack(ItemStack itemStack) {
        this.amount = String.valueOf(itemStack.getAmount());
    }

    public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        return MathUtil.getNumber(ChatUtil.translateRaw(this.amount, uuid, replacementSource)).map((bigDecimal) -> {
            return bigDecimal.intValue() >= itemStack.getAmount();
        }).orElse(false);
    }

    public AmountModifier setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public AmountModifier setAmount(int amount) {
        this.amount = String.valueOf(amount);
        return this;
    }
}
