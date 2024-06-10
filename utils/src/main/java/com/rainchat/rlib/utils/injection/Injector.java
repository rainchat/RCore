package com.rainchat.rlib.utils.injection;

import java.lang.reflect.Constructor;

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

                    return clazz.cast(constructor.newInstance(initargs));
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

}

