package ru.rainchat.rlib.commands.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TabComplete {
    String[] value();
}
