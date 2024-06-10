package com.rainchat.rlib.utils.config;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JsonLoader<T> {

    private final JsonConfig<T> jsonConfig;
    private Set<T> set;

    public JsonLoader(String string, Plugin plugin) {
        this.jsonConfig = new JsonConfig<>(string, plugin);
    }

    public JsonLoader(String string, String path) {
        this.jsonConfig = new JsonConfig<>(string, path);
    }


    public void load(Type type) {
        set = jsonConfig.read(type);
        if(jsonConfig.read(type) == null) set = new HashSet<>();
    }

    public void unload() {
        jsonConfig.write(set);
    }

    public void add(T t) {
        set.add(t);
    }

    public void remove(T t) {
        set.remove(t);
    }

    protected Set<T> toSet() {
        return Collections.unmodifiableSet(set);
    }
}
