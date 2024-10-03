package de.kwantux.networks.component.module;

import de.kwantux.networks.config.Config;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Acceptor extends PassiveModule {
    boolean accept(ItemStack stack);

    static boolean spaceFree(Inventory inv, ItemStack stack) {
        if (Config.complexInventoryChecks) {
            int amount = stack.getAmount(); // The amount of items that need to be transmitted
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null) return true; // Empty slot
                if (item.isSimilar(stack)
                    || item.isSimilar(stack)) {
                    amount -=                        // Decrease the amount of items that need to be transmitted
                            (64 - item.getAmount()); // by the amount of items that can be put into the slot
                    if (amount <= 0) return true;
                }
            }
            return false;
        }
        else return inv.firstEmpty() != -1;
    }

    int acceptorPriority();

    void incrementAcceptorPriority();
    void decrementAcceptorPriority();
}
