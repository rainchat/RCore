package ru.rainchat.rlib.utils;

import ru.rainchat.rlib.utils.hooks.EconomyBridge;
import ru.rainchat.rlib.utils.hooks.PlaceholderAPIBridge;
import ru.rainchat.rlib.utils.scheduler.RScheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RUtility {

    private static JavaPlugin INSTANCE;
    private static ServerLog serverLog;
    private static boolean debug = false;

    /**
     * Gets instance.
     *
     * @return Instance.
     */
    public static JavaPlugin getInstance() {
        return RUtility.INSTANCE;
    }

    public static void initialize(JavaPlugin plugin) {
        if (RUtility.INSTANCE != null) return;
        RUtility.INSTANCE = Objects.requireNonNull(plugin, "plugin cannot be null!");
        serverLog = new ServerLog();
        serverLog.setup(INSTANCE);

        PlaceholderAPIBridge placeholderAPIBridge = new PlaceholderAPIBridge();
        placeholderAPIBridge.setupPlugin();
        if (PlaceholderAPIBridge.hasValidPlugin()) {
            serverLog.info("Successfully hooked into PlaceholderAPI.");
        }
        if (EconomyBridge.setupEconomy()) {
            serverLog.info("Successfully hooked into Economy vault.");
        } else {
            serverLog.warning("Vault with a compatible economy plugin was not found! Icons with a PRICE or commands that give money will not work.");
        }

    }

    public static void setDebug(boolean debug) {
        RUtility.debug = debug;
    }

    public static void debug(String message) {
        if (debug) serverLog.info(message);
    }

    public static void debug() {
        debug(" ");
    }

    public static void debugHr() {
        debug("---------------------------------------------------------------");
    }

    public static RScheduler asyncScheduler() {
        return new RScheduler(RUtility.INSTANCE, true);
    }

    public static RScheduler syncScheduler() {
        return new RScheduler(RUtility.INSTANCE, false);
    }
}
