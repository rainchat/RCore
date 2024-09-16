package ru.rainchat.rlib.utils.injection;

public interface Context {
    <T> T get(Class<T> type);
    <T> void bind(T instance);
}

