package dev.nanoflux.networks.commands;


import dev.nanoflux.networks.Main;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;

public abstract class CommandHandler {
    protected final Main plugin;
    protected final CommandManager<CommandSender> cmd;

    protected CommandHandler(Main plugin, CommandManager<CommandSender> commandManager) {
        this.plugin = plugin;
        this.cmd = commandManager;
    }

    public abstract void register();
}
