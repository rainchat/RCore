package ru.rainchat.rlib.utils.builder;

import ru.rainchat.rlib.utils.collections.CaseInsensitiveStringHashMap;
import ru.rainchat.rlib.utils.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The builder
 *
 * @param <R> the type of the raw value
 * @param <V> the type of the final value
 */
public class Builder<R, V> {
    private final Map<String, BiFunction<String, R, V>> functionMap = new CaseInsensitiveStringHashMap<>();
    private final Map<String, String> nameMap = new CaseInsensitiveStringHashMap<>();

    /**
     * Get the function map
     *
     * @return the function map
     */
    public Map<String, BiFunction<String, R, V>> getFunctionMap() {
        return Collections.unmodifiableMap(functionMap);
    }

    /**
     * Get the name map
     *
     * @return the name map
     */
    public Map<String, String> getNameMap() {
        return Collections.unmodifiableMap(nameMap);
    }

    /**
     * Register a new function
     *
     * @param biFunction the function
     * @param name       the name of the modifier
     * @param aliases    the aliases of the modifier
     */
    public void register(BiFunction<String, R, V> biFunction, String name, String... aliases) {
        functionMap.put(name, biFunction);
        nameMap.put(name, name);
        for (String alias : aliases) {
            nameMap.put(alias, name);
        }
    }

    /**
     * Register a new function
     *
     * @param function the function
     * @param name     the name of the modifier
     * @param aliases  the aliases of the modifier
     */
    public void register(Function<R, V> function, String name, String... aliases) {
        register((s, r) -> function.apply(r), name, aliases);
    }

    /**
     * Unregister a modifier
     *
     * @param name the name of the modifier
     */
    public void unregister(String name) {
        functionMap.remove(name);
        nameMap.values().removeIf(s -> s.equalsIgnoreCase(name));
    }

    /**
     * Remove a name from the name map
     *
     * @param name the name
     * @throws IllegalArgumentException if the function map contains the name
     */
    public void removeName(String name) {
        if (functionMap.containsKey(name)) {
            throw new IllegalArgumentException("You can not remove the key name of the function");
        }
        nameMap.remove(name);
    }

    /**
     * Unregister all modifiers
     */
    public void unregisterAll() {
        functionMap.clear();
        nameMap.clear();
    }

    /**
     * Build the final value from a raw value
     *
     * @param name     the name or the alias of the function
     * @param rawValue the raw value
     * @return the final value
     */
    public Optional<V> build(String name, R rawValue) {
        return Optional.ofNullable(nameMap.get(name)).map(functionMap::get).map(function -> function.apply(name, rawValue));
    }

    public List<Optional<V>> buildAll() {
        return CollectionUtils.createStringListFromObject(functionMap.keySet(), true)
                .stream()
                .map(string -> build(string, (R) ""))
                .collect(Collectors.toList());
    }


    /**
     * Build the map of final values
     *
     * @param rawMap the map of raw values
     * @return the map of final values
     */
    public Map<String, V> build(Map<String, R> rawMap) {
        Map<String, V> map = new CaseInsensitiveStringHashMap<>();
        rawMap.forEach((name, raw) -> build(name, raw).ifPresent(v -> map.put(name, v)));
        return map;
    }
}