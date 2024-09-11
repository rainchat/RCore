package com.rainchat.rlib.messages.placeholder.base;

public interface PlaceholderSupply<T> {
    Class<T> forClass();

    String getReplacement(String forKey);
}
