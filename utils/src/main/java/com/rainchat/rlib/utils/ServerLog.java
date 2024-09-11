package com.rainchat.rlib.utils;


import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class ServerLog {

    public String prefix;
    public Plugin plugin;

    public ServerLog setup(Plugin pluginS) {
        plugin = pluginS;
        prefix = "[" + pluginS.getName() + "] ";
        return this;
    }

    public void log(Level level, String text) {
        plugin.getLogger().log(level, prefix + text);
    }

    public void info(String text) {
        Bukkit.getLogger().info(prefix + " " + text);
    }

    public void warning(String text) {
        Bukkit.getLogger().warning(prefix + " " + text);
    }

    public void error(String text) {
        Bukkit.getLogger().severe(prefix + " " + text);
    }

    public void exception(StackTraceElement[] stackTraceElement, String text) {
        info("(!) " + prefix + " has being encountered an error, pasting below for support (!)");
        for (StackTraceElement traceElement : stackTraceElement) {
            error(traceElement.toString());
        }
        info("Message: " + text);
        info(prefix + " version: " + plugin.getDescription().getVersion());
        info("Please report this error to me on spigot");
        info("(!) " + prefix + " (!)");
    }
}
