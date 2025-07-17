package de.kwantux.networks.component.module;

import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.Origin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface BaseModule {
    BlockLocation pos();
    Origin origin();
    boolean isLoaded();
    boolean ready();
    Inventory inventory();

    static boolean spaceFree(Inventory inv, ItemStack stack) {
        if (Config.complexInventoryChecks) {
            int amount = stack.getAmount(); // The amount of items that need to be transmitted
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null) return true; // Empty slot
                if (item.isSimilar(stack)
                        || item.isSimilar(stack)) {
                    amount -=                        // Decrease the amount of items that need to be transmitted
                            (item.getMaxStackSize() - item.getAmount()); // by the amount of items that can be put into the slot
                    if (amount <= 0) return true;
                }
            }
            return false;
        }
        return inv.firstEmpty() != -1;
    }

    default boolean spaceFree(ItemStack stack) {
        return spaceFree(inventory(), stack);
    }
}
