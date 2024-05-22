package com.rainchat.rlib.messages.placeholder;

public interface PlaceholderSupply<T> {
    Class<T> forClass();

    String getReplacement(String forKey);
}
