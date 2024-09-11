package com.rainchat.rlib.core;

import com.rainchat.rlib.commands.CommandController;
import com.rainchat.rlib.utils.RUtility;
import com.rainchat.rlib.utils.config.yaml.YamlConfig;
import com.rainchat.rlib.utils.injection.Injector;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class RainPlugin extends JavaPlugin {

    Injector injector = new Injector();
    CommandController commandController;

    protected abstract void enable();

    protected abstract void disable();

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void onEnable() {
        RUtility.initialize(this);
        bind(this);
        commandController = new CommandController(this);
        enable();
    }

    public YamlConfig getYamlConfig(String path) {
        return new YamlConfig(this, path);
    }

    public <T> void bind(T object) {
        injector.bind(object);
    }

    public <T> T inject(Class<T> clazz) {
        return injector.inject(clazz);
    }

    public <T> T getBindClass(Class<T> clazz) {
        return injector.getBind(clazz);
    }

    public void registerCommand(Class<?> clazz) {
        commandController.registerCommands(inject(clazz));
    }

    public void registerCommand(Object object) {
        commandController.registerCommands(object);
    }

    public void registerEvent(Listener object) {
        this.getServer().getPluginManager().registerEvents(object, this);
    }

    public void registerEvent(Class<? extends Listener> clazz) {
        this.getServer().getPluginManager().registerEvents(inject(clazz), this);
    }

    public Injector getInjector() {
        return injector;
    }

    public CommandController getCommandController() {
        return commandController;
    }

}
