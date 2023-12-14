package dev.nanoflux.networks.storage;

import dev.nanoflux.networks.utils.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InterThreadTransmissionController {
    //TODO: Implement ITTC for Folia Support

    private static List<Transaction> transactions = new ArrayList<Transaction>();
    public static void transmit(ItemStack stack, Location source, Location target) {
        transactions.add(new Transaction(stack, source, target));
    }
}
