package com.rainchat.rlib.storage.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    boolean primaryKey() default false;
    boolean autoIncrement() default false;
    int length() default 255; // Default length for string fields
    boolean isBlob() default false; // Indicate if the column is a BLOB
}

