package de.kwantux.networks;

import de.kwantux.networks.component.module.*;
import de.kwantux.networks.utils.FoliaUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.logging.Level;

import static de.kwantux.networks.Main.logger;
import static de.kwantux.networks.config.Config.ranges;

public class Sorter {

    public static synchronized void transmit(@Nonnull ItemStack stack, BaseModule source, BaseModule target) {
        source.inventory().removeItem(stack);
        target.inventory().addItem(stack);
    }

    public static synchronized void donate(Network network, Donator donator) {
        List<? extends Acceptor> acceptors = network.acceptors();
        for (ItemStack item : donator.donate()) {
            if (item == null) continue;
            try {
                for (Acceptor acceptor : acceptors) {
                    if (!(acceptor.ready() && inDistance(network,donator,acceptor))) continue;
                    if (!acceptor.accept(item)) continue;
                    if (!acceptor.spaceFree(item)) continue;
                    transmit(item, donator, acceptor);
                    break;
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) logger.severe("Failed to sort item: " + e.getMessage());
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
    }

    public static synchronized void request(Network network, Requestor requestor) {
        List<? extends Supplier> suppliers = network.suppliers();
        for (ItemStack item : requestor.requested()) {
            if (item == null) continue;
            try {
                for (Supplier supplier : suppliers) {
                    if (supplier.ready() && inDistance(network, requestor, supplier) && supplier.supply().contains(item)) {
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

    public static boolean inDistance(Network network, ActiveModule active, PassiveModule passive) {
        return
                ranges[Math.min(active.range(), ranges.length - 1)] < 1 // If component range is set to infinity
                        || // or if
                        active.pos().getDistance(passive.pos()) <= // distance is smaller than
                        ranges[Math.min(active.range(), ranges.length - 1)] + network.range(); // component range + network range
    }
}
