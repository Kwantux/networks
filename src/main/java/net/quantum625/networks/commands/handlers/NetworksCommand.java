package net.quantum625.networks.commands.handlers;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.quantum625.config.lang.Language;
import net.quantum625.networks.Main;
import net.quantum625.networks.commands.CommandHandler;
import net.quantum625.networks.commands.CommandManager;
import org.bukkit.command.CommandSender;


public class NetworksCommand extends CommandHandler {

    Language lang;

    public NetworksCommand(Main plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::help)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("create")
                .argument(StringArgument.of("id"))
                .handler(this::create)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::delete)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::select)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::list)
        );
    }

    private void help(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }

    private void create(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }

    private void delete(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }

    private void select(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }

    private void list(CommandContext<CommandSender> context) {
        context.getSender().sendMessage(Component.text("Networks - V2.0"));
    }



}
