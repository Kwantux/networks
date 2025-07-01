package de.kwantux.networks.utils;

import net.kyori.adventure.text.Component;

import java.util.HashMap;

public abstract class Origin {

    public static HashMap<String, Class<? extends Origin>> classes = new HashMap<>();
    public static HashMap<Class<? extends Origin>, String> tags = new HashMap<>();

    static {
        BlockLocation.register();
    }

    public static void register(Class<? extends Origin> clazz, String tag) {
        classes.put(tag, clazz);
        tags.put(clazz, tag);
    }

    public abstract String toString();
    public abstract Component displayText();
}
