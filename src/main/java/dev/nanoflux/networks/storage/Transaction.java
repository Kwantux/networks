package dev.nanoflux.networks.storage;

import dev.nanoflux.networks.utils.Location;
import org.bukkit.inventory.ItemStack;

public record Transaction(ItemStack stack, Location source, Location target) {}
