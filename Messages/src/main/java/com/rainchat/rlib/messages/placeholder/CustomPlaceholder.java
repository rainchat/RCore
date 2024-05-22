package com.rainchat.rlib.messages.placeholder;

public abstract class CustomPlaceholder<T> implements PlaceholderSupply<T> {
    private final String prefix;

    public CustomPlaceholder() {
        this("");
    }

    public CustomPlaceholder(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Class<T> forClass() {
        return null;
    }

    @Override
    public final String getReplacement(String forKey) {
        return getPrefixedReplacement(prefix, forKey);
    }

    final String getPrefixedReplacement(String prefix, String forKey) {

        if (forKey.startsWith(prefix)) {
            forKey = forKey.substring(prefix.length());
        }

        if (forKey.startsWith("_")) {
            return getReplacement(forKey.substring(1), forKey);
        }
        return getReplacement(forKey, forKey);
    }

    protected abstract String getReplacement(String base, String fullKey);


    public String getPrefix() {
        return prefix;
    }
}