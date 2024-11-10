package de.kwantux.networks.component.util;

import de.kwantux.networks.Main;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Item Filters are stored as hashes of item meta. <br /><br />
 * This translations list is used to get the item name from just the hash when a player wants to inspect the filters of a Sorting Container.<br />
 * (Otherwise it would just show a bunch of random numbers) <br /><br />
 * Whenever a new item filter is added, it has to be added here. <br /><br />
 * This class is purely QoL, essential functions should not depend on this.
 */
public class FilterTranslator {

    private static Path path = null;

    private static Map<Integer, Component> translations = new HashMap<>();


    public static Component translate(int id) {
        return Objects.requireNonNullElse(translations.get(id), Component.text("#" + id));
    }

    private static void generateMaterialTranslations() throws IOException {
        Main.logger.info("Generating translations mappings for item filters...");
        for (int i = 1; i < Material.values().length; i++) {
            String translationKey = Material.values()[i].getItemTranslationKey();
            if (translationKey == null) continue;
            translations.put(i, Component.translatable(translationKey)
                    .hoverEvent(HoverEvent.showItem(
                            HoverEvent.ShowItem.showItem(
                                    Key.key(Material.values()[i].name().toLowerCase()), 1
                            )
                    ))
            );
        }
        save();
        Main.logger.info("Done generating translations mappings for item filters.");
    }

    public static void updateTranslation(int id, Component translation) {
        translations.put(id, translation);
    }

    public static void save() throws IOException {
        if (path == null) return;
        List<String> lines = new ArrayList<>();

        lines.add("v\t" + Bukkit.getMinecraftVersion());
        lines.addAll(translations.entrySet().stream().map(e ->
                e.getKey() + "\t" + MiniMessage.miniMessage().serialize(e.getValue())
        ).collect(Collectors.toSet()));

        Files.write(path, lines);
    }

    public static void load(Path filePath) throws IOException {
        path = filePath;
        String minecraftVersion = null;
        try {
            for (String line : Files.readAllLines(filePath)) {
                String[] split = line.split("\t");
                if (split[0].equals("v")) {
                    minecraftVersion = split[1];
                    continue;
                }
                int id = Integer.parseInt(split[0]);
                Component translation = MiniMessage.miniMessage().deserialize(split[1]);
                translations.put(id, translation);
            }
        } catch (NoSuchFileException ignored) { // If there is no file, we have nothing to load
        }
        if (!Objects.equals(minecraftVersion, Bukkit.getMinecraftVersion()))
            generateMaterialTranslations();
    }

}
