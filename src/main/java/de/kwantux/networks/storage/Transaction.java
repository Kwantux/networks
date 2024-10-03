package de.kwantux.networks.storage;

import de.kwantux.networks.component.module.BaseModule;
import org.bukkit.inventory.ItemStack;

public record Transaction(ItemStack stack, BaseModule source, BaseModule target) {}
