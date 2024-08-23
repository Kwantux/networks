package dev.nanoflux.networks;

import dev.nanoflux.networks.storage.InterThreadTransmissionController;
import dev.nanoflux.networks.component.module.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static dev.nanoflux.networks.Main.logger;

public class Sorter {

    private static Integer[] ranges;
    private static boolean ittc = false;

    public static void setConfig(@Nonnull Config config) {
        ranges = config.getMaxRanges();
    }

    public static void transmit(@Nonnull ItemStack stack, BaseModule source, BaseModule target) {
        if (ittc) {
            InterThreadTransmissionController.transmit(stack, source, target);
        }
        else {
            source.inventory().remove(stack);
            target.inventory().addItem(stack);
        }
    }

    public static void donate(Network network, Donator donator) {
        List<? extends Acceptor> acceptors = network.acceptors();
        for (ItemStack item : donator.donate()) {
            if (item == null) continue;
            try {
                for (Acceptor acceptor : acceptors) {
                    if (acceptor.ready() && acceptor.pos().getDistance(donator.pos()) <= ranges[Math.min(donator.range(), ranges.length - 1)] + network.range() && Acceptor.spaceFree(acceptor.inventory(), item) && acceptor.accept(item)) {
                        transmit(item, donator, acceptor);
                        break;
                    }
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) logger.severe("Failed to sort item: " + e.getMessage());
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
    }

    public static void request(Network network, Requestor requestor) {
        List<? extends Supplier> suppliers = network.suppliers();
        for (ItemStack item : requestor.requested()) {
            if (item == null) continue;
            try {
                for (Supplier supplier : suppliers) {
                    if (supplier.ready() && supplier.pos().getDistance(requestor.pos()) <= ranges[Math.min(requestor.range(), ranges.length - 1)] + network.range() && supplier.supply().contains(item)) {
                        transmit(item, supplier, requestor);
                        break;
                    }
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) logger.severe("Failed to sort item: " + e.getMessage());
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
    }
}
