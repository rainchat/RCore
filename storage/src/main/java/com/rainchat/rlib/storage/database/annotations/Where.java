package com.rainchat.rlib.storage.database.annotations;



import com.rainchat.rlib.storage.database.common.DataType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Where {
	public String fieldName();
	public DataType type();
}
