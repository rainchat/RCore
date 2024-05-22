package com.rainchat.rlib.commands.parse;

import com.rainchat.rlib.commands.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class TypeParser {

    private final Map<Class<?>, Parser> parsers = new HashMap<>();

    public TypeParser() {
        registerParser(Boolean.TYPE, Boolean::parseBoolean);
        registerParser(Byte.TYPE, Byte::parseByte);
        registerParser(Short.TYPE, Short::parseShort);
        registerParser(Integer.TYPE, Integer::parseInt);
        registerParser(Float.TYPE, Float::parseFloat);
        registerParser(Long.TYPE, Long::parseLong);
        registerParser(Double.TYPE, Double::parseDouble);
        registerParser(String.class, (string) -> string);
        registerParser(Player.class, (string) -> {
            Player p = Bukkit.getPlayer(string);
            if (p == null) {
                throw new IllegalArgumentException("There is no online player with the name \"" + string + "\"");
            } else {
                return p;
            }
        });
    }

    public void registerParser(Class<?> type, Parser objParser) {
        parsers.put(type, objParser);
    }

    public Object parseObject(Class<?> type, String parse) throws CommandException {
        Parser objParser = parsers.get(type);
        if (objParser != null) {
            return objParser.parseObject(parse);
        }
        if (type.isEnum()) {
            return parseEnum(type, parse);
        }
        throw new CommandException("No registered parser for " + type.getCanonicalName() + ".");
    }

    private static Object parseEnum(Class<?> enumType, String arg) throws CommandException {
        for (Object constant : enumType.getEnumConstants()) {
            if (constant.toString().equalsIgnoreCase(arg)) {
                return constant;
            }
        }
        throw new CommandException(arg + " is not a valid value for " + enumType.getCanonicalName() + ".");
    }

    public boolean parserExistsFor(Class<?> type) {
        return parsers.containsKey(type) || type.isEnum();
    }

}
