package de.kwantux.networks.component.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Item Filters are stored as hashes of item meta. <br /><br />
 * This translations list is used to get the item name from just the hash when a player wants to inspect the filters of a Sorting Container.<br />
 * (Otherwise it would just show a bunch of random numbers) <br /><br />
 * Whenever a new item filter is added, it has to be added here. <br /><br />
 * This class is purely QoL, essential functions should not depend on this.
 */
public class FilterTranslator {

    private static Path path = null;

    private static final Map<Integer, Component> translations = new HashMap<>();


    public static Component translate(int id) {
        return Objects.requireNonNullElse(translations.get(id), Component.text("#" + id));
    }

    public static boolean hasTranslation(int id) {
        return translations.containsKey(id);
    }

    public static void updateTranslation(int id, Component translation) {
        translations.put(id, translation);
    }

    public static void save() throws IOException {
        if (path == null) return;

        List<String> lines = translations.entrySet().stream().map(e ->
                e.getKey() + "\t" + MiniMessage.miniMessage().serialize(e.getValue())
        ).distinct().collect(Collectors.toList());

        Files.write(path, lines);
    }

    public static void load(Path filePath) throws IOException {
        path = filePath;
        try {
            for (String line : Files.readAllLines(filePath)) {
                String[] split = line.split("\t");
                try {
                    int id = Integer.parseInt(split[0]);
                    Component translation = MiniMessage.miniMessage().deserialize(split[1]);
                    translations.put(id, translation);
                } catch (NumberFormatException ignored) {}
            }
        } catch (NoSuchFileException ignored) {}
    }

}
