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
        int matId = materialHash(stack);
        int metaHash = metaHash(stack);
        if (metaHash == BLANK_META_HASH) return matId;
        return matId + metaHash;
    }

    public static int materialHash(@Nonnull ItemStack stack) {
        return stack.getType().ordinal();
    }

    private static int metaHash(@Nonnull ItemStack stack) {
        return stack.getItemMeta().getAsString().hashCode();
    }
}
