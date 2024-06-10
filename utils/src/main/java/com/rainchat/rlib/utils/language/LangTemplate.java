package com.rainchat.rlib.utils.language;
import com.rainchat.rlib.utils.config.YamlConfig;
import com.rainchat.rlib.utils.general.Reflex;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class LangTemplate {


    protected Plugin plugin;
    protected YamlConfig config;
    private static LangTemplate parent;

    public LangTemplate(Plugin plugin, YamlConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public static LangTemplate getParent() {
        return parent;
    }

    public static void setTemplate(LangTemplate langTemplate) {
        parent = langTemplate;
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
        this.load();
        this.config.save();
        setTemplate(this);
    }

    public String getMessage(String path, String def) {
        return LangMessageUtil.getMessage(config.getOriginal(), path, def);
    }

    public String getMessage(LangMsg massage) {
        return LangMessageUtil.getMessage(config.getOriginal(), massage.getPath(), massage.getMsgDefault());
    }

    private void load() {
        for (Field field : Reflex.getFields(this.getClass())) {
            if (!LangMsg.class.isAssignableFrom(field.getType())) {
                continue;
            }

            LangMsg jmsg;
            try {
                jmsg = (LangMsg) field.get(this);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            if (jmsg.getPath() == null) {
                jmsg.setPath(field.getName()); // Set the path to String in config
            }

            // Fill message fields from extended class with parent message field values.
            if (!field.getDeclaringClass().equals(this.getClass())) {
                LangMsg superField = (LangMsg) Reflex.getFieldValue(this.parent, field.getName());
                if (superField != null) {
                    jmsg.setMessage(superField.getMessage());
                    continue;
                }
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
        }
        this.config.save();
    }

}