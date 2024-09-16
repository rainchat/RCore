package ru.rainchat.rlib.menumodule.ui.pagination;

import ru.rainchat.rlib.menumodule.ui.buttons.SelectionButton;
import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.inventory.items.modifiers.AmountModifier;
import ru.rainchat.rlib.inventory.items.modifiers.MaterialModifier;
import ru.rainchat.rlib.inventory.menus.BaseClickItem;
import ru.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class TestPagination extends OnlinePlayersPagination {
    private final List<BaseClickItem> clickableItems = new ArrayList<>();

    @Override
    public void setupItems() {
        this.clickableItems.clear();

        for (int i = 1; i < 50; i++) {

            SelectionButton clickItem = getPageItem();
            BaseItem baseItem = clickItem.getBaseItem();

            baseItem.addItemModifier((new AmountModifier()).setAmount(i));
            baseItem.addItemModifier(new MaterialModifier().setMaterial(Material.ACACIA_BUTTON));

            clickItem.setItem(baseItem);
            clickItem.setInventory(getMenu());

            List<PlaceholderSupply<?>> supplies = getPlaceholders();

            clickItem.getBaseItem().setStringReplacer(supplies);

            this.clickableItems.add(clickItem);
        }

        this.setItems(this.clickableItems);
    }

}
