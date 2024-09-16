package ru.rainchat.rlib.menumodule.builders;

import ru.rainchat.rlib.menumodule.ui.actions.*;
import ru.rainchat.rlib.menumodule.ui.actions.requirements.EcoReq;
import ru.rainchat.rlib.menumodule.ui.actions.requirements.GamemodeReq;
import ru.rainchat.rlib.menumodule.ui.actions.requirements.LevelReq;
import ru.rainchat.rlib.menumodule.ui.actions.requirements.PermissionsReq;
import ru.rainchat.rlib.inventory.items.BaseItem;
import ru.rainchat.rlib.inventory.menus.PaginationMenu;
import ru.rainchat.rlib.menumodule.ui.actions.*;
import ru.rainchat.rlib.utils.builder.Builder;
import ru.rainchat.rlib.utils.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ActionBuilder extends Builder<String, Action> {

    public static final ActionBuilder INSTANCE = new ActionBuilder();

    private ActionBuilder() {
        registerDefaultActions();
    }

    private void registerDefaultActions() {
        register(MessageGlobalAction::new, "global-message");
        register(MessageAction::new, "message");
        register(PlayerAction::new, "player");
        register(OpAction::new, "op", "admin");
        register(ServerAction::new, "console", "server");
        register(DelayAction::new, "delay");
        register(CancelAction::new, "cancel");
        //page
        register(PageAction::new, "page");
        register(UpdateAction::new, "update");

        //req
        register(LevelReq::new, "req-level");
        register(PermissionsReq::new, "req-permission");
        register(GamemodeReq::new, "req-gamemode");
        register(EcoReq::new, "req-money");
    }

    /**
     * Build a list of actions
     *
     * @param menu   the menu involved in
     * @param object the object
     * @return the list of actions
     */
    public List<Action> getActions(PaginationMenu menu, BaseItem baseItem, Object object) {
        return CollectionUtils.createStringListFromObject(object, true)
                .stream()
                .map(string -> {
                    String[] split = string.split(":", 2);
                    String name = split[0];
                    String value = split.length > 1 ? split[1] : "";

                    Action action = build(name.trim(), value.trim()).orElseGet(() -> new PlayerAction(string.trim()));
                    action.setMenu(menu);
                    action.setItem(baseItem);
                    return action;
                })
                .collect(Collectors.toList());
    }
}