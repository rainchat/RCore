package com.rainchat.rlib.commands.messages;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * A class for sending messages to players and consoles, with info and error headers.
 *
 * @author Christopher Bishop
 */
public class MessageSender {

    private String infoPrefix;
    private String errorPrefix;

    public MessageSender() {
        this("", ChatColor.RED + "");
    }

    public MessageSender(String infoPrefix, String errorPrefix) {
        this.infoPrefix = (infoPrefix == null) ? "" : infoPrefix;
        this.errorPrefix = (errorPrefix == null) ? this.infoPrefix : errorPrefix;
    }

    public void send(CommandSender sender, String... messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    public void info(CommandSender sender, String... messages) {
        for (String message : messages) {
            send(sender, infoPrefix + message);
        }
    }

    public void error(CommandSender sender, String... messages) {
        for (String message : messages) {
            send(sender, errorPrefix + message);
        }
    }

}
