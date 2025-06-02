package de.kwantux.networks.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ItemHash {

    public static final int BLANK_META_HASH;

    static {
        ItemStack blank = new ItemStack(Material.STONE);
        BLANK_META_HASH = blank.getItemMeta().hashCode();
    }

    public static int hash(@Nonnull ItemStack stack) {
        int matId = stack.getType().ordinal();
        int metaHash = stack.getItemMeta().hashCode();
        if (metaHash == BLANK_META_HASH) return matId;
        return matId + metaHash;
    }

    public static int fusedHash(@Nonnull ItemStack stack) {
        int matId = stack.getType().ordinal();
        int metaHash = stack.getItemMeta().hashCode();
        return matId + metaHash;
    }

    public static int materialHash(@Nonnull ItemStack stack) {
        return stack.getType().ordinal();
    }

    public static int metaHash(@Nonnull ItemStack stack) {
        return stack.getItemMeta().hashCode();
    }
}
