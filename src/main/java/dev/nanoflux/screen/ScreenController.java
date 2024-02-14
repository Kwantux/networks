package dev.nanoflux.screen;

import org.bukkit.plugin.java.JavaPlugin;

public class ScreenController {

    private JavaPlugin plugin;

    public ScreenController(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Screen createScreen(String name) {
        return new Screen(plugin, name);
    }
}
