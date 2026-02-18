package de.kwantux.networks;

import de.kwantux.networks.component.module.*;
import de.kwantux.networks.utils.FoliaUtils;
import de.kwantux.networks.utils.PositionedItemStack;
import de.kwantux.networks.utils.Transaction;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static de.kwantux.networks.Main.logger;
import static de.kwantux.networks.config.Config.ranges;

public class Sorter {

    /**
     * Transmit an item stack from one module to another
     */
    public static synchronized void transmit(@Nonnull Transaction transaction) {
        if (transaction.target().spaceFree(transaction.stack()))
            if (removeItem(transaction))
                addItemNoCheck(transaction);
    }

    /**
     * Perform the item addition part of a transaction
     */
    private static synchronized void addItemNoCheck(Transaction transaction) {
        transaction.target().inventory().addItem(transaction.stack());
    }

    /**
     * Perform the item addition part of a transaction
     */
    public static synchronized boolean addItem(Transaction transaction) {
        if (!transaction.target().spaceFree(transaction.stack())) return false;
        addItemNoCheck(transaction);
        return true;
    }

    /**
     * Perform the item removal part of a transaction
     */
    public static synchronized boolean removeItem(Transaction transaction) {
        boolean itemExists = containsRequestedStack(transaction.source().inventory().getStorageContents(), transaction.stack());
        if (!itemExists) return false;
        transaction.source().inventory().removeItem(transaction.stack());
        return true;
    }

    /**
     * Trigger a donation event for a donator component
     * @param network The network to donate to
     * @param donator The donator component
     */
    public static synchronized void donate(Network network, Donator donator) {
        donate(network, donator, donator.donate());
    }

    /**
     * Donate a list of item stacks to a network as an active module
     * @param network The network to donate to
     * @param source The donating module
     * @param items The items to donate
     */
    public static synchronized void donate(Network network, ActiveModule source, Set<PositionedItemStack> items) {
        for (
                Transaction transaction : tryDonation(network, source, items).stream()
                .sorted(
                        Comparator.comparingInt((Transaction transaction) -> transaction.stack().getAmount()).reversed()
                ).toList()) {
            transmit(transaction);
        }
    }

    /**
     * Try to donate a list of item stacks to a network and return the possible transactions
     * @param network The network to donate to
     * @param source The donating module
     * @param items The items to donate
     * @return The list of possible transactions
     */
    public static synchronized Set<Transaction> tryDonation(Network network, ActiveModule source, Set<PositionedItemStack> items) {
        Set<Transaction> transactions = new HashSet<>();
        for (PositionedItemStack item : items) {
            if (item == null) continue;
            for (Acceptor acceptor : network.acceptors()) {
                if (!(acceptor.ready() && inDistance(network, source,acceptor))) continue;
                if (!acceptor.accept(item)) continue;
                if (!acceptor.spaceFree(item)) continue;
                transactions.add( new Transaction(source, acceptor, item));
            }
        }
        return transactions;
    }

    /**
     * Trigger a request event for a requestor component
     * @param network The network to request from
     * @param requestor The requestor component
     */
    public static synchronized void request(Network network, Requestor requestor) {
        request(network, requestor, requestor.requested());
    }

    /**
     * Initiates a request for a list of item stacks from the network to the specified target module.
     * The method attempts to fulfill the request by contacting suppliers within the network
     * and transmitting the items if a valid transaction is possible.
     *
     * @param network The network from which the items are requested
     * @param target The target module that will receive the items
     * @param items The list of item stacks to be requested from the network
     */
    public static synchronized void request(Network network, ActiveModule target, Set<PositionedItemStack> items) {
        for (
                Transaction transaction : tryRequest(network, target, items).stream()
                .sorted(
                        Comparator.comparingInt((Transaction transaction) -> transaction.stack().getAmount()).reversed()
                ).toList()) {
            transmit(transaction);
        }
    }

    /**
     * Try to request a list of item stacks from a network and return the possible transactions
     * @param network The network to request from
     * @param target The target module
     * @param items The items to request
     * @return The list of possible transactions
     */
    public static synchronized Set<Transaction> tryRequest(Network network, ActiveModule target, Set<PositionedItemStack> items) {
        Set<Transaction> transactions = new HashSet<>();
        List<? extends Supplier> suppliers = network.suppliers();
        for (PositionedItemStack item : items) {
            if (item == null) continue;
            try {
                for (Supplier supplier : suppliers) {
                    if (supplier.ready() && inDistance(network, target, supplier) && containsRequestedStack(supplier.supply(), item)) {
                        transactions.add(new Transaction(supplier, target, item));
                        break;
                    }
                }
            }
            catch (Throwable e) {
                if (!FoliaUtils.folia) {
                    logger.severe("Failed to sort item: " + e.getMessage());
                    e.printStackTrace();
                }
                else logger.log(Level.FINER, "Failed to sort item (this will be regularly thrown if Folia is used): " + e.getMessage());
            } // Folia compatibility
        }
        return transactions;
    }

    /**
     * Check if a passive module is in range of an active module
     * @param network The network
     * @param active The active module
     * @param passive The passive module
     * @return True if the passive module is in range
     */
    public static boolean inDistance(Network network, ActiveModule active, BaseModule passive) {
        return
                active.range() < 0 || active.range() >= ranges.length || ranges[Math.min(active.range(), ranges.length - 1)] < 1 // If component range is set to infinity
                        || // or if
                        active.pos().getDistance(passive.pos()) <= // distance is smaller than
                        ranges[Math.min(active.range(), ranges.length - 1)] + network.range(); // component range + network range
    }

    public static boolean containsRequestedStack(List<ItemStack> stacks, ItemStack requested) {
        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            if (stack.isSimilar(requested)){
                if (stack.getAmount() >= requested.getAmount()) return true;
            }
        }
        return false;
    }

    public static boolean containsRequestedStack(ItemStack[] stacks, ItemStack requested) {
        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            if (stack.isSimilar(requested)){
                if (stack.getAmount() >= requested.getAmount()) return true;
            }
        }
        return false;
    }
}
