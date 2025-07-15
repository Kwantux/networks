package de.kwantux.networks.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ItemHash {

    public static final int BLANK_META_HASH;

    static {
        ItemStack blank = new ItemStack(Material.STONE);
        BLANK_META_HASH = metaHash(blank);
    }

    public static int strictHash(@Nonnull ItemStack stack) {
        int matHash = materialHash(stack);
        int metaHash = metaHash(stack);
        if (metaHash == BLANK_META_HASH) return matHash;
        return matHash + metaHash;
    }

    public static int materialHash(@Nonnull ItemStack stack) {
        return stack.getType().getKey().hashCode();
    }

    private static int metaHash(@Nonnull ItemStack stack) {
        return stack.getItemMeta().getAsString().hashCode();
    }
}
