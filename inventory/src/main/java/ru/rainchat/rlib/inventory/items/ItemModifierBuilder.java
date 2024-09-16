package ru.rainchat.rlib.inventory.items;


import ru.rainchat.rlib.inventory.items.modifiers.*;
import ru.rainchat.rlib.utils.builder.Builder;
import ru.rainchat.rlib.inventory.items.modifiers.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemModifierBuilder extends Builder<Object, ItemModifier> {

    public static final ItemModifierBuilder INSTANCE = new ItemModifierBuilder();

    private ItemModifierBuilder() {
        registerDefaultItemModifiers();
    }

    private void registerDefaultItemModifiers() {
        register(NameModifier::new, "name");
        register(LoreModifier::new, "lore");
        register(AmountModifier::new, "amount");
        register(DurabilityModifier::new, "durability", "damage");
        register(MaterialModifier::new, "raw-material", "material" , "raw-id", "raw-mat");
        register(ItemFlagModifier::new, "flag", "item-flags", "itemflag", "itemflags", "item-flag");
        register(NBTModifier::new, "nbt", "nbt-data");
    }

    /**
     * Register the item modifier
     *
     * @param itemModifierSupplier the item modifier factory
     * @param name                 the name of the item modifier
     * @param aliases              the aliases of the item modifier
     */
    public void register(Supplier<ItemModifier> itemModifierSupplier, String name, String... aliases) {
        register(o -> {
            ItemModifier itemModifier = itemModifierSupplier.get();
            itemModifier.loadFromObject(o);
            return itemModifier;
        }, name, aliases);
    }

    /**
     * Build the list of item modifiers
     *
     * @param section the section
     * @return the list of the modifiers
     */
    public List<ItemModifier> getItemModifiers(Map<String, Object> section) {
        return section.entrySet()
                .stream()
                .flatMap(entry -> build(entry.getKey(), entry.getValue()).map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }
}
