package de.kwantux.networks.commands;

import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.component.util.ComponentType;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.ParserRegistry;

import java.util.List;

import static de.kwantux.networks.Main.lang;

public final class NetworksCommandManager {

    private  LegacyPaperCommandManager<CommandSender> commandManager;
    private  ParserRegistry<CommandSender> parsers;

    public NetworksCommandManager(final Main plugin) {

        commandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        parsers = commandManager.parserRegistry();

        // Register argument parsers
        parsers.registerParserSupplier(TypeToken.get(Network.class), op -> new NetworkParser());
        parsers.registerParserSupplier(TypeToken.get(ComponentType.class), op -> new ComponentTypeParser());

        // Register caption provider
        commandManager.captionRegistry().registerProvider(
                CaptionProvider.forCaption(Caption.of("argument.parse.failure.network"), sender -> {
                    try {
                        return lang.getRaw("invalidnetwork");
                    } catch (InvalidNodeException e) {
                        return "No such network: <input>";
                    }
                })
        );
        commandManager.captionRegistry().registerProvider(
                CaptionProvider.forCaption(Caption.of("argument.parse.failure.componenttype"), sender -> "No such component type: <input>")
        );

        // Register command handlers
        List.of(
            new NetworksCommand(plugin, commandManager),
            new TeleportCommand(plugin, commandManager)
        ).forEach(CommandHandler::register);
    }
}
