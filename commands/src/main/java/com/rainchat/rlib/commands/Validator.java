package com.rainchat.rlib.commands;

import com.rainchat.rlib.commands.messages.MessageSender;
import org.bukkit.command.CommandSender;

/**
 * A static class that validates data.
 *
 * @author Christopher Bishop
 */
@SuppressWarnings("ALL")
public final class Validator {

    public static boolean isInteger(String str, CommandSender sender, MessageSender msgSender) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            if (sender != null && msgSender != null) msgSender.error(sender, "Invalid integer \"" + str + "\".");
            return false;
        }
    }

    public static boolean isInteger(String str) {
        return isInteger(str, null, null);
    }

    public static boolean isFloat(String str, CommandSender sender, MessageSender msgSender) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            if (sender != null && msgSender != null) msgSender.error(sender, "Invalid integer \"" + str + "\".");
            return false;
        }
    }

    public static boolean isFloat(String str) {
        return isFloat(str, null, null);
    }

    public static boolean isDouble(String str, CommandSender sender, MessageSender msgSender) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            if (sender != null && msgSender != null) msgSender.error(sender, "Invalid number \"" + str + "\".");
            return false;
        }
    }

    public static boolean isDouble(String str) {
        return isDouble(str, null, null);
    }

    public static boolean isBoolean(String str, CommandSender sender, MessageSender msgSender) {
        try {
            Boolean.parseBoolean(str);
            return true;
        } catch (NumberFormatException e) {
            if (sender != null && msgSender != null) msgSender.error(sender, "Invalid number \"" + str + "\".");
            return false;
        }
    }

    public static boolean isBoolean(String str) {
        return isBoolean(str, null, null);
    }

}
