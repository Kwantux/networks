package net.quantum625.networks.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Queue;
public final class NetworkArgument<C> extends CommandArgument<C, Network> {

    static NetworkManager net;

    private NetworkArgument(
            final @NonNull NetworkManager networkManager,
            final @NonNull String name,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(true, name, new NetworkParser<C>(), Network.class);
        net = networkManager;
    }

    public static final class NetworkParser<C> implements ArgumentParser<C, Network> {
        public NetworkParser() {
        }

        public @NonNull ArgumentParseResult<Network> parse(final @NonNull CommandContext<C> commandContext, final @NonNull Queue<@NonNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(IntegerArgument.IntegerParser.class, commandContext));
            }

            Network network = net.getFromID(input);

            if (network == null) {
                return ArgumentParseResult.failure(new InvalidNetworkException(commandContext, input));
            }

            return ArgumentParseResult.success(network);
        }
    }
}
