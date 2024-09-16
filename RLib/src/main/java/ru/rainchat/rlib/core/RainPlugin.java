package ru.rainchat.rlib.core;

import ru.rainchat.rlib.commands.CommandController;
import ru.rainchat.rlib.core.libraries.LibraryLoader;
import ru.rainchat.rlib.utils.RUtility;
import ru.rainchat.rlib.utils.config.yaml.YamlConfig;
import ru.rainchat.rlib.utils.injection.Injector;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class RainPlugin extends JavaPlugin {

    Injector injector = new Injector();
    CommandController commandController;

    protected abstract void enable();

    protected abstract void disable();

    @Override
    public void onLoad() {
        LibraryLoader loader = new LibraryLoader(this, new File(getDataFolder(), "libraries"));

        loader.loadLibrary("com.github.rainchat.rcore", "EventManager", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "inventory", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "MenuModule", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "commands", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "messages", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "storage", "2.1", "https://jitpack.io");
        loader.loadLibrary("com.github.rainchat.rcore", "utils", "2.1", "https://jitpack.io");


    }

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
