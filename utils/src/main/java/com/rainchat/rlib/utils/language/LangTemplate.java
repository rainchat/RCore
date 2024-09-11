package com.rainchat.rlib.utils.language;

import com.rainchat.rlib.messages.ChatUtil;
import com.rainchat.rlib.messages.placeholder.base.PlaceholderSupply;
import com.rainchat.rlib.utils.config.yaml.YamlConfig;
import com.rainchat.rlib.utils.general.Reflex;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangTemplate {

    protected Plugin plugin;
    protected YamlConfig config;
    private List<LangMsg> langMessages = new ArrayList<>();
    private LangMsg prefix;

    public LangTemplate(Plugin plugin, YamlConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.prefix = new LangMsg("Messages.prefix", "[DefaultPrefix]");
        langMessages.add(prefix);
    }

    public LangTemplate(Plugin plugin, YamlConfig config, String path) {
        this.plugin = plugin;
        this.config = config;
        this.prefix = new LangMsg("Messages.prefix", path);
        langMessages.add(prefix);
    }

    public void update(YamlConfig config) {
        config.reload();
        this.config = config;
        setup();
    }

    public void update() {
        config.reload();
        setup();
    }

    public void setup() {
        this.load(this);
        this.config.save();
    }

    public String getMessage(String path, String def) {
        return replacePlaceholders(LangMessageUtil.getMessage(config.getOriginal(), path, def));
    }

    public String getMessage(LangMsg message) {
        // Check if the message is already initialized
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            return replacePlaceholders(message.getMessage());
        }
        // If not initialized, retrieve from configuration
        return replacePlaceholders(LangMessageUtil.getMessage(config.getOriginal(), message.getPath(), message.getMsgDefault()));
    }

    public void load(Object instance) {
        Class<?> clazz = instance.getClass();
        for (Field field : Reflex.getFields(clazz)) {
            if (!LangMsg.class.isAssignableFrom(field.getType())) {
                continue;
            }

            LangMsg jmsg;
            try {
                jmsg = (LangMsg) field.get(instance);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            if (jmsg.getPath() == null) {
                jmsg.setPath(field.getName()); // Set the path to String in config
            }

            String path = jmsg.getPath();
            FileConfiguration cfg = this.config.getOriginal();

            // Add missing lang node in config.
            if (!cfg.contains(path)) {
                String msg = jmsg.getMsgDefault();
                String[] split = msg.split("\n");
                cfg.set(path, split.length > 1 ? Arrays.asList(split) : msg);
            }

            // Load message text from lang config
            String msgLoad;
            List<String> cList = cfg.getStringList(path);
            if (!cList.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                cList.forEach(line -> {
                    if (builder.length() > 0)
                        builder.append("\\n");
                    builder.append(line);
                });
                msgLoad = builder.toString();
            } else {
                msgLoad = cfg.getString(path, "");
            }
            jmsg.setMessage(msgLoad);

            // Add the LangMsg to the list
            langMessages.add(jmsg);
        }
        this.config.save();
    }

    public void reloadMessages() {
        for (LangMsg message : langMessages) {
            String path = message.getPath();
            FileConfiguration cfg = this.config.getOriginal();

            // Load message text from lang config
            String msgLoad;
            List<String> cList = cfg.getStringList(path);
            if (!cList.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                cList.forEach(line -> {
                    if (builder.length() > 0)
                        builder.append("\\n");
                    builder.append(line);
                });
                msgLoad = builder.toString();
            } else {
                msgLoad = cfg.getString(path, "");
            }
            message.setMessage(msgLoad);
        }
    }

    public void sendMessage(CommandSender sender, LangMsg message, PlaceholderSupply<?>... replacementSource) {
        ChatUtil.sendTranslation(sender, getMessage(message), replacementSource);
    }

    private String replacePlaceholders(String message) {
        if (message.startsWith("!!")) return message.substring(2).replace("%prefix%", prefix.getMessage());
        return prefix.getMessage() + message.replace("%prefix%", prefix.getMessage());
    }
}
