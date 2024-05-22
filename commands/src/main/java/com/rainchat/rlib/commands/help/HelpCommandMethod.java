package com.rainchat.rlib.commands.help;

import com.rainchat.rlib.commands.CommandException;
import com.rainchat.rlib.commands.messages.MessageSender;
import com.rainchat.rlib.commands.Validator;
import com.rainchat.rlib.commands.command.CommandMethod;
import com.rainchat.rlib.commands.command.CommandHandler;
import com.rainchat.rlib.commands.command.CommandNode;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandMethod implements CommandMethod {

    private String usage;

    public HelpCommandMethod(CommandHandler root, String name) {
        this.usage = "/" + root.getName() + " " + name + " [page]";
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public Class<?>[] getArgumentTypes() {
        return new Class<?>[]{Integer.class};
    }

    @Override
    public void invoke(CommandHandler handler, CommandSender sender, String[] rawArgs, MessageSender msgSender) throws CommandException {
        CommandNode root = handler.getParent();
        List<String> helpMessages = new ArrayList<>();
        buildHelpMessages(helpMessages, root);

        int pageLength = 7;
        int maxPages = (int) Math.ceil(helpMessages.size() / (double) pageLength);
        int page = 0;

        if (rawArgs.length > 0) {
            if (!Validator.isInteger(rawArgs[0], sender, msgSender)) return;
            page = Integer.parseInt(rawArgs[0]) - 1;
        }

        if (page > maxPages - 1) {
            page = maxPages - 1;
        } else if (page < 0) {
            page = 0;
        }

        msgSender.info(sender, root.getName().toUpperCase() + " Commands | Page " + (page + 1) + "/" + maxPages);
        msgSender.send(sender, "------------------------------------------------");
        msgSender.send(sender, CommandUtils.pagination(helpMessages, pageLength, page));
    }


    private void buildHelpMessages(List<String> helpMessages, CommandNode container) {
        if (container instanceof CommandHandler && ((CommandHandler) container).getMethod() != null) {
            CommandHandler handler = (CommandHandler) container;
            helpMessages.add(handler.getMethod().getUsage() + (handler.getDescription().length() > 0 ? " - " + handler.getDescription() : ""));
        }

        // Build messages for children.
        for (CommandNode chc : container.getChildren()) {
            buildHelpMessages(helpMessages, chc);
        }
    }

}
