package de.kwantux.networks.component.module;

import org.bukkit.inventory.ItemStack;

import java.util.function.IntSupplier;

public interface Acceptor extends PassiveModule {
    boolean accept(ItemStack stack);

    /**
     * Performance-optimized accept overload.
     * <p>
     * The caller pre-computes the (cheap) material hash once per item and supplies a
     * <i>memoized</i> {@link IntSupplier} for the (expensive) strict hash so that the full
     * item NBT is serialized at most once per item — instead of once per (item × acceptor).
     * <p>
     * Default implementation falls back to {@link #accept(ItemStack)} so existing
     * acceptors (e.g. MiscContainer) keep working unchanged.
     *
     * @param stack      the item stack being offered
     * @param matHash    pre-computed material hash (see ItemHash.materialHash)
     * @param strictHash memoized supplier of the strict hash (see ItemHash.strictHash);
     *                   only call getAsInt() when strict matching is actually required
     */
    default boolean accept(ItemStack stack, int matHash, IntSupplier strictHash) {
        return accept(stack);
    }

    int acceptorPriority();

    void incrementAcceptorPriority();
    void decrementAcceptorPriority();
}
