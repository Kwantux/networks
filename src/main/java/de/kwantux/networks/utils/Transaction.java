package de.kwantux.networks.utils;

import de.kwantux.networks.component.module.BaseModule;
import org.bukkit.inventory.ItemStack;

public record Transaction(BaseModule source, BaseModule target, ItemStack stack) {
}
