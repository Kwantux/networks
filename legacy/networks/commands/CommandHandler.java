package quantum625.networks.commands;


import dev.nanoflux.networks.Main;

public abstract class CommandHandler {
    protected final Main plugin;
    protected final CommandManager commandManager;

    protected CommandHandler(Main plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
