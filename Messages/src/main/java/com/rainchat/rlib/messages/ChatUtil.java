package com.rainchat.rlib.messages;

import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.messages.ractionbar.ActionBarHolder;
import com.rainchat.rlib.messages.tellraw.TellrawHolder;
import com.rainchat.rlib.messages.title.TitleHolder;
import de.themoep.minedown.MineDown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    private static final Pattern PH_KEY = Pattern.compile("%([\\w\\._-]+)%");

    private static final Pattern chatPattern = Pattern
            .compile("\\s*%(chat|tellraw|title|subtitle|actionbar)%\\s*((?:(?!%(?:chat|tellraw|title|subtitle|actionbar)%).)*)");

    public static String format(String string) {
        return Color.parseHexString(string);
    }

    public static void sendTranslation(CommandSender p, String text, PlaceholderSupply<?>... replacementSource) {
        Player player = p instanceof Player ? (Player) p : null;
        text = translateRaw(text, replacementSource);

        if (text.isEmpty())
            return;

        int tellrawPos = text.indexOf("%tellraw%");
        int chatPos = text.indexOf("%chat%");
        int titlePos = text.indexOf("%title%");
        int subtitlePos = text.indexOf("%subtitle%");
        int actionbarPos = text.indexOf("%actionbar%");
        if ((tellrawPos & titlePos & subtitlePos & actionbarPos & chatPos) == -1) {
            p.spigot().sendMessage(MineDown.parse(Color.parseHexString(text)));
            return;
        }

        if (player != null) {

            StringBuilder textBuilder = new StringBuilder();
            StringBuilder tellrawBuilder = new StringBuilder();
            StringBuilder titleBuilder = new StringBuilder();
            StringBuilder subtitleBuilder = new StringBuilder();
            StringBuilder actionbarBuilder = new StringBuilder();

            Matcher matcher = chatPattern.matcher(text);

            while (matcher.find()) {

                String type = matcher.group(1);
                String message = matcher.group(2);
                switch (type) {
                    case "tellraw" -> tellrawBuilder.append(message);
                    case "chat" -> textBuilder.append(message).append("\n");
                    case "title" -> titleBuilder.append(message);
                    case "subtitle" -> subtitleBuilder.append(message);
                    case "actionbar" -> actionbarBuilder.append(message);
                    default -> {
                    }
                }
                String before = text.substring(0, matcher.start());
                String after = text.substring(matcher.end());
                text = before + after;
                matcher = chatPattern.matcher(text);
            }

            String tellrawMessage = tellrawBuilder.toString();
            String textMessage = textBuilder.toString();
            String titleMessage = titleBuilder.toString();
            String subtitleMessage = subtitleBuilder.toString();
            String actionbarMessage = actionbarBuilder.toString();


            if (!text.isEmpty()) {
                player.sendMessage(text);
            }
            if (!textMessage.isEmpty()) {
                player.sendMessage(textMessage);
            }
            if (!tellrawMessage.isEmpty()) {
                TellrawHolder.tellraw(player, tellrawMessage);
            }
            if (!titleMessage.isEmpty() || !subtitleMessage.isEmpty()) {
                TitleHolder.sendTitle(player, titleMessage, subtitleMessage, 10, 70, 20);
            }
            if (!actionbarMessage.isEmpty()) {
                ActionBarHolder.sendActionbar(player, actionbarMessage);
            }
        }
    }

    public static String translateRaw(String template, PlaceholderSupply<?>... replacementSource) {
        Matcher m = PH_KEY.matcher(template);

        while (m.find()) {
            for (PlaceholderSupply<?> e : replacementSource) {
                String replacement = e.getReplacement(m.group(1));
                if (replacement == null) {
                    continue;
                }
                if (replacement.isEmpty()) {
                    continue;
                }

                template = template.replace(m.group(), replacement);
            }
        }

        return Color.parseHexString(template);
    }


    public static String translateRaw(String template, UUID uuid, List<PlaceholderSupply<?>> replacementSource) {
        Matcher m = PH_KEY.matcher(template);

        while (m.find()) {

            for (PlaceholderSupply<?> e : replacementSource) {
                String replacement = e.getReplacement(m.group(1));
                if (replacement == null) {
                    continue;
                }
                if (replacement.isEmpty()) {
                    continue;
                }


                template = template.replace(m.group(), replacement);
            }
        }

        return Color.parseHexString(template);
    }

    public static List<String> translateRaw(List<String> list, PlaceholderSupply<?>... replacementSource) {
        List<String> tempList = new ArrayList<>();
        list.forEach(template -> tempList.add(translateRaw(template, replacementSource)));
        return tempList;
    }
}
