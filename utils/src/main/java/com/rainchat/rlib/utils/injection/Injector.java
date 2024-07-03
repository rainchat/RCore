package com.rainchat.rlib.utils.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Injector {
    private final Context context;

    public Injector() {
        this.context = new SimpleContext();
    }

    public Injector(Context context) {
        this.context = context;
    }

    public <T> void bind(T instance) {
        context.bind(instance);
    }

    public <T> T inject(Class<T> clazz) {
        try {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(InjectClass.class)) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] initargs = new Object[paramTypes.length];

                    for (int i = 0; i < paramTypes.length; i++) {
                        Object arg = getBind(paramTypes[i]);
                        if (arg == null) {
                            throw new IllegalArgumentException("No bound instance for " + paramTypes[i].getName());
                        }
                        initargs[i] = arg;
                    }

                    T instance = clazz.cast(constructor.newInstance(initargs));
                    injectDependencies(instance); // Inject dependencies into fields
                    return instance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("No suitable constructor found for " + clazz.getName());
    }

    public <T> T getBind(Class<T> clazz) {
        return context.get(clazz);
    }

    public void injectDependencies(Object instance) {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Dependency.class)) {
                Object dependency = getBind(field.getType());
                if (dependency == null) {
                    throw new IllegalArgumentException("No bound instance for " + field.getType().getName());
                }
                field.setAccessible(true);
                try {
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}