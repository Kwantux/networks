package de.kwantux.networks.commands;

import de.kwantux.networks.Network;
import io.leangen.geantyref.TypeToken;
import de.kwantux.networks.Main;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.ParserRegistry;

public final class NetworksCommandManager {

    private  LegacyPaperCommandManager<CommandSender> commandManager;
    private  ParserRegistry<CommandSender> parsers;

    public NetworksCommandManager(final Main plugin) {

        commandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        parsers = commandManager.parserRegistry();

        // Register argument parsers
        parsers.registerParserSupplier(TypeToken.get(Network.class), op -> new NetworkParser());

        // Register command handlers
        new NetworksCommand(plugin, commandManager).register();
    }
}
