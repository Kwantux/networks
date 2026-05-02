package de.kwantux.networks.utils;

import de.kwantux.networks.component.util.FilterTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ItemHash {

    public static int strictHash(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int matHash = materialHash(stack.getType());
        int metaHash = metaHash(stack);
        int hash = matHash + metaHash;
        if (!FilterTranslator.hasTranslation(hash)) FilterTranslator.updateTranslation(hash, Component.text("#").append(stack.effectiveName().hoverEvent(HoverEvent.showItem(
                HoverEvent.ShowItem.showItem(
                        Key.key(stack.getType().name().toLowerCase()), 1
                )
        ))));
        return hash;
    }

    public static int materialHash(@Nonnull Material material) {
        return material.getKey().hashCode();
    }
    public static int materialHash(@Nonnull ItemStack stack) {
        int hash = materialHash(stack.getType());
        if (stack.isEmpty()) return 0;
        if (!FilterTranslator.hasTranslation(hash)) FilterTranslator.updateTranslation(hash, stack.effectiveName().hoverEvent(HoverEvent.showItem(
                HoverEvent.ShowItem.showItem(
                        Key.key(stack.getType().name().toLowerCase()), 1
                )
        )));
        return hash;
    }

    private static int metaHash(@Nonnull ItemStack stack) {
        return stack.getItemMeta().getAsString().hashCode();
    }
}
