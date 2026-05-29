package de.kwantux.networks.commands;


import de.kwantux.networks.Main;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.Source;

public abstract class CommandHandler {
    protected final Main plugin;
    protected final CommandManager<Source> cmd;

    protected CommandHandler(Main plugin, CommandManager<Source> commandManager) {
        this.plugin = plugin;
        this.cmd = commandManager;
    }

    public abstract void register();
}
