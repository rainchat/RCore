package com.rainchat.rlib.commands.command;

import com.rainchat.rlib.commands.annotaion.Completion;
import com.rainchat.rlib.commands.completion.CustomTabCompleter;
import com.rainchat.rlib.commands.messages.MessageSender;
import com.rainchat.rlib.commands.CommandException;
import com.rainchat.rlib.commands.parse.TypeParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandHandler extends CommandNode implements CommandExecutor {

    private CommandMethod method;

    private String[] params;

    public CommandHandler(String name, MessageSender msgSender, CustomTabCompleter tabCompleter, TypeParser parser) {
        super(name, msgSender, tabCompleter, parser);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if it belongs to sub-command.
        if (args.length > 0) {
            String sub = args[0];

            CommandHandler handler = getHandler(sub);
            if (handler != null) {
                // Remove first arg.
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);

                return handler.onCommand(sender, cmd, label + " " + sub, newArgs);
            }
        }

        // Execute command.
        if (method == null) {
            getMessageSender().error(sender, "Invalid command " + label);
        } else {
            try {
                method.invoke(this, sender, args, getMessageSender());
            } catch (CommandException e) {
                if (e.getCause() != null) {
                    e.printStackTrace();
                    getMessageSender().error(sender, "Internal server error.");
                } else {
                    getMessageSender().error(sender, e.getMessage());
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {


        List<String> completions = super.onTabComplete(sender, cmd, label, args);
        if (method == null) return completions;

        Class<?>[] types = method.getArgumentTypes();

        if (types.length >= args.length) {
            Class<?> type = types[args.length - 1];
            String arg = args[args.length - 1];

            if (method instanceof CommandMethodWrapper) {
                Completion completion = ((CommandMethodWrapper) method).getMethod().getAnnotation(Completion.class);
                completions = new ArrayList<>();
                if (completion != null) {
                    String[] value = completion.value();

                    if (value.length < args.length) return completions;;
                    List<String> values = getTabCompleter().getCompletions(value[args.length-1], sender, type);
                    getContainsMatches(arg, values, completions);
                }

            } else if (type == Player.class || type == OfflinePlayer.class) {
                // Tab complete players.
                List<String> players = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    players.add(p.getName());
                }
                StringUtil.copyPartialMatches(arg, players, completions);
            } else if (type.isEnum()) {
                // Tab complete enums.
                List<String> values = new ArrayList<>();
                for (Object value : type.getEnumConstants()) {
                    values.add(value.toString());
                }
                StringUtil.copyPartialMatches(arg, values, completions);
            }
        }
        return completions;
    }

    public static void getContainsMatches(String arg, List<String> values, List<String> completions) {
        List<String> filteredValues = values.stream()
                .filter(value -> value.contains(arg))
                .toList();

        completions.addAll(filteredValues);
    }

    public CommandMethod getMethod() {
        return method;
    }

    public void setMethod(CommandMethod method) {
        this.method = method;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
