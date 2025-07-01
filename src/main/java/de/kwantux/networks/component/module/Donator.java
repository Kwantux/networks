package de.kwantux.networks.component.module;

import de.kwantux.networks.utils.PositionedItemStack;

import java.util.Set;

public interface Donator extends ActiveModule {
    Set<PositionedItemStack> donate();
}
