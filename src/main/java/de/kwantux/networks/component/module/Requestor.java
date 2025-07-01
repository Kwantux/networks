package de.kwantux.networks.component.module;

import de.kwantux.networks.utils.PositionedItemStack;

import java.util.Set;

public interface Requestor extends ActiveModule {
    Set<PositionedItemStack> requested();
}
