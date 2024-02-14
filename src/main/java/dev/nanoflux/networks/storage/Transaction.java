package dev.nanoflux.networks.storage;

import dev.nanoflux.networks.component.module.BaseModule;
import org.bukkit.inventory.ItemStack;

public record Transaction(ItemStack stack, BaseModule source, BaseModule target) {}
