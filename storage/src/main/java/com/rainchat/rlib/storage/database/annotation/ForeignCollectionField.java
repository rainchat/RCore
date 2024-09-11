package com.rainchat.rlib.storage.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignCollectionField {
    String mappedBy();
}
