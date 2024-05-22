package com.rainchat.rlib.commands.command;

import com.rainchat.rlib.commands.completion.CustomTabCompleter;
import com.rainchat.rlib.commands.messages.MessageSender;
import com.rainchat.rlib.commands.parse.TypeParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandNode implements TabCompleter {

    private String name;
    private String[] aliases = new String[0];
    private String description = "";
    private MessageSender msgSender;
    private final CustomTabCompleter tabCompleter;
    private final TypeParser parser;
    private CommandNode parent = null;
    private List<CommandNode> children = new ArrayList<>();

    public CommandNode(String name, MessageSender msgSender, CustomTabCompleter tabCompleter, TypeParser parser) {
        this.name = name;
        this.msgSender = msgSender;
        this.tabCompleter = tabCompleter;
        this.parser = parser;
    }

    public CommandNode getParent() {
        return parent;
    }

    public void setParent(CommandNode parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        if (parent != null) {
            parent.children.add(this);
        }
        this.parent = parent;
    }

    public CommandNode[] getChildren() {
        return children.toArray(new CommandNode[0]);
    }

    public CommandHandler getHandler(String name) {
        return getHandler(name, false);
    }

    public CommandHandler getHandler(String name, boolean create) {
        for (CommandNode handlerContainer : children) {
            if (isHandlerWithName(handlerContainer, name)) {
                return (CommandHandler) handlerContainer;
            }
        }
        if (create) return createNewHandler(name);
        return null;
    }

    private boolean isHandlerWithName(CommandNode handlerContainer, String name) {
        return handlerContainer instanceof CommandHandler && handlerContainer.isValidName(name);
    }

    private CommandHandler createNewHandler(String name) {
        CommandHandler newHandler = new CommandHandler(name, msgSender, tabCompleter, parser);
        newHandler.setParent(this);
        return newHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) {
            return handleSubCommandCompletion(args, sender, cmd, label);
        } else {
            return handleCommandCompletion(args, completions);
        }
    }

    private List<String> handleSubCommandCompletion(String[] args, CommandSender sender, Command cmd, String label) {
        String subCommand = args[0];
        for (CommandNode child : children) {
            if (child.isValidName(subCommand)) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return child.onTabComplete(sender, cmd, label + " " + subCommand, newArgs);
            }
        }
        return Collections.emptyList();
    }

    private List<String> handleCommandCompletion(String[] args, List<String> completions) {
        List<String> commands = children.stream()
                .map(CommandNode::getName)
                .collect(Collectors.toList());
        StringUtil.copyPartialMatches(args[0], commands, completions);
        return completions;
    }

    // Getters and Setters moved to the bottom
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public boolean isValidName(String name) {
        if (name.equals(this.name)) {
            return true;
        }
        return Arrays.asList(this.aliases).contains(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MessageSender getMessageSender() {
        return msgSender;
    }

    public void setMessageSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public CustomTabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public TypeParser getParser() {
        return parser;
    }

}

