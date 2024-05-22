package com.rainchat.rlib.commands;

import com.rainchat.rlib.commands.annotaion.Command;
import com.rainchat.rlib.commands.command.CommandMethodWrapper;
import com.rainchat.rlib.commands.command.CommandHandler;
import com.rainchat.rlib.commands.command.CommandNode;
import com.rainchat.rlib.commands.completion.CustomTabCompleter;
import com.rainchat.rlib.commands.help.HelpCommandMethod;
import com.rainchat.rlib.commands.messages.MessageSender;
import com.rainchat.rlib.commands.parse.TypeParser;
import com.rainchat.rlib.utils.RUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandRegister extends CommandNode {

    private JavaPlugin plugin;

    public CommandRegister(JavaPlugin plugin, MessageSender msgSender) {
        super(null, msgSender, new CustomTabCompleter(), new TypeParser());
        this.plugin = plugin;
    }

    public void registerCommands(Object object, MessageSender msgSender) {
        RUtility.debug("Registering " + object.getClass().getCanonicalName() + ": ");
        RUtility.debugHr();

        RUtility.debug("Looking for @Command...");
        for (Method method : object.getClass().getMethods()) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) continue;
            RUtility.debug("\tAttempting to register §b" + method.getName() + "§r:");

            try {
                CommandMethodWrapper commandMethod = CommandMethodWrapper.build(object, method, getParser());
                Command command = commandMethod.getCommandAnnotation();
                String[] struct = command.structure().split(" ");

                CommandHandler handler = getHandler(struct);
                handler.setMessageSender(msgSender);
                handler.setAliases(Arrays.copyOf(command.aliases(), command.aliases().length));
                handler.setDescription(command.desc());
                handler.setMethod(commandMethod);

                // Register permissions
                for (String perm : cmd.perms()) {
                    if(Bukkit.getPluginManager().getPermission(perm) == null) {
                        Bukkit.getPluginManager().addPermission(new Permission(perm));
                        RUtility.debug("\t\tRegistered permission §b" + perm + "§r /w Spigot");
                    }
                    else {
                        RUtility.debug("\t\tPermission §b" + perm + "§r already registered /w Spigot");
                    }
                }
                // Register Command
                CommandHandler root = registerCommand(handler);
                if (root != null)
                    RUtility.debug("\t\tRegistered command §b" + root.getName() + "§r /w Spigot");

                RUtility.debug("§a\t\tSUCCESS§r");
            } catch (CommandException e) {
                RUtility.debug("§c\t\tFAILED: " + e.getMessage() + "§r");
            }

        }

        RUtility.debug();
    }

    public void registerCommands(Object object) {
        registerCommands(object, getMessageSender());
    }

    public void registerHelpCommand(String name, String helpName, MessageSender msgSender) {
        RUtility.debug("Registering Help Command For: /" + name);
        RUtility.debugHr();

        CommandHandler root = getHandler(name);
        if (root == null) {
            RUtility.debug("§c\tFailed: Cannot find command. Is it registered? §r");
            return;
        }

        CommandHandler handler = getHandler(new String[]{root.getName(), helpName});
        //handler.setParams(getp);
        handler.setMessageSender(msgSender);
        handler.setMethod(new HelpCommandMethod(root, helpName));

        RUtility.debug("§a\tSUCCESS§r");
        RUtility.debug();
    }

    public void registerHelpCommand(String name, String helpName) {
        registerHelpCommand(name, helpName, getMessageSender());
    }

    public void registerHelpCommands(String helpName) {
        for (CommandNode child : getChildren()) {
            registerHelpCommand(child.getName(), helpName);
        }
    }

    public void registerHelpCommands() {
        registerHelpCommands("help");
    }

    private CommandHandler registerCommand(CommandHandler handler) throws CommandException {
        CommandHandler root = findRootHandler(handler);

        PluginCommand cmd = plugin.getCommand(root.getName());
        if (cmd == null) {
            cmd = createPluginCommand(root);
            registerPluginCommand(cmd, root);
        } else if (!cmd.getPlugin().equals(plugin)) {
            throw new CommandException("Your plugin does not own \"" + root.getName() + "\"");
        }

        if (handler == root) {
            updatePluginCommand(cmd, root, handler);
        }
        return cmd.isRegistered() ? root : null;
    }

    private CommandHandler findRootHandler(CommandHandler handler) {
        while (handler.getParent() != this) {
            handler = (CommandHandler) handler.getParent();
        }
        return handler;
    }

    private PluginCommand createPluginCommand(CommandHandler root) throws CommandException {
        try {
            Constructor<PluginCommand> con = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            con.setAccessible(true);
            PluginCommand cmd = con.newInstance(root.getName(), plugin);
            cmd.setLabel(root.getName());
            cmd.setExecutor(root);
            cmd.setTabCompleter(root);
            return cmd;
        } catch (Exception e) {
            throw new CommandException("Failed to create PluginCommand: " + e.getMessage(), e);
        }
    }

    private void registerPluginCommand(PluginCommand cmd, CommandHandler root) throws CommandException {
        CommandMap commandMap = getCommandMap();
        commandMap.register(root.getName(), cmd);
    }

    private void updatePluginCommand(PluginCommand cmd, CommandHandler root, CommandHandler handler) throws CommandException {
        CommandMap commandMap = getCommandMap();
        cmd.unregister(commandMap);
        cmd.setAliases(new ArrayList<>(Arrays.asList(root.getAliases())));
        cmd.setUsage(handler.getMethod().getUsage());
        cmd.setDescription(handler.getDescription());
        commandMap.register(root.getName(), cmd);
    }


    private CommandHandler getHandler(String[] structure) {
        return getHandler(this, structure, 0);
    }

    private CommandHandler getHandler(CommandNode container, String[] structure, int offset) {
        if (offset == structure.length - 1) {
            return container.getHandler(structure[offset], true);
        } else {
            return getHandler(container.getHandler(structure[offset], true), structure, offset + 1);
        }
    }

    private CommandMap getCommandMap() throws CommandException {
        // Register Command
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            commandMap.setAccessible(true);
            CommandMap map = ((CommandMap) commandMap.get(Bukkit.getServer()));
            commandMap.setAccessible(false);

            return map;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new CommandException(e.getClass().getSimpleName() + " - " + e.getMessage(), e);
        }
    }

}
