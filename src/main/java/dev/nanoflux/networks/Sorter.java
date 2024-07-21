package dev.nanoflux.networks;

import dev.nanoflux.networks.storage.InterThreadTransmissionController;
import dev.nanoflux.networks.component.module.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

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
            for (Acceptor acceptor : acceptors) {
                if (acceptor.pos().getDistance(donator.pos()) <= ranges[donator.range()] + network.range() && acceptor.accept(item)) {
                    transmit(item, donator, acceptor);
                    break;
                }
            }
        }
    }

    public static void request(Network network, Requestor requestor) {
        List<? extends Supplier> suppliers = network.suppliers();
        for (ItemStack item : requestor.requested()) {
            for (Supplier supplier : suppliers) {
                if (supplier.pos().getDistance(requestor.pos()) >= ranges[requestor.range()] + network.range() && supplier.supply().contains(item)) {
                    transmit(item, supplier, requestor);
                    break;
                }
            }
        }
    }
}
