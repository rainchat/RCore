package ru.rainchat.rlib.utils.language;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class LangMessageUtil {

    public static String getMessage(FileConfiguration configuration, String path, String def) {
        String message;
        boolean isList = isList(configuration, path);
        boolean exists = exists(configuration, path);

        if (isList) {
            if (exists) {
                message = convertList(configuration.getStringList(path));
            } else {
                message = def;
            }
        } else {
            if (exists) {
                message = configuration.getString(path);
            } else {
                message = def;
            }
        }

        return message;
    }

    public static String getMessage(FileConfiguration configuration, String path) {
        String message;
        boolean isList = isList(configuration, path);
        boolean exists = exists(configuration, path);

        if (isList) {
            if (exists) {
                message = convertList(configuration.getStringList(path));
            } else {
                message = "";
            }
        } else {
            if (exists) {
                message = configuration.getString(path);
            } else {
                message = "";
            }
        }

        return message;
    }

    private static String convertList(List<String> list) {
        String message = "";
        for (String line : list) {
            message += line + "\n";
        }
        return message;
    }

    private static boolean exists(FileConfiguration configuration, String path) {
        return configuration.contains(path);
    }

    private static boolean isList(FileConfiguration configuration, String path) {
        if (configuration.contains(path)) {
            return !configuration.getStringList(path).isEmpty();
        } else {
            return false;
        }
    }

}
