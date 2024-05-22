package com.rainchat.rlib.utils.injection;

import java.util.HashMap;
import java.util.Map;

public class SimpleContext implements Context {
    private final Map<Class<?>, Object> values = new HashMap<>();

    @Override
    public <T> T get(Class<T> type) {
        return type.cast(values.get(type));
    }

    @Override
    public <T> void bind(T instance) {
        values.put(instance.getClass(), instance);
    }
}

