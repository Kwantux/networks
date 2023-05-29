package net.quantum625.networks.commands.handlers;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.quantum625.networks.Main;
import net.quantum625.networks.commands.CommandHandler;
import net.quantum625.networks.commands.CommandManager;
import org.bukkit.command.CommandSender;


public class AdminCommands extends CommandHandler {

    public AdminCommands(Main plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::help)
                .argument(IntegerArgument.of("network"))
        );
    }

    private void help(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }

}
