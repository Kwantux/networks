package de.kwantux.screen.option;

import de.kwantux.screen.InventoryMenu;
import org.bukkit.inventory.ItemStack;

public abstract class ConfigOption<T> {

    protected ItemStack display;

    protected InventoryMenu menu;
    protected String name;

    protected ConfigOption(InventoryMenu menu, String name) {
        this.menu = menu;
        this.name = name;
    }

    public abstract T value();

    public ItemStack display() {
        return display;
    }
    public abstract void onClick();


    public Class<?> type() {
        return value().getClass();
    }
}
