package de.kwantux.networks.commands;

import static de.kwantux.networks.Main.mgr;

import de.kwantux.networks.Network;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;

import org.bukkit.entity.Player;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public final class NetworkParser implements ArgumentParser<Source, Network>, BlockingSuggestionProvider.Strings<Source> {


    public static @NonNull ParserDescriptor<Source, Network> networkParser() {
        return ParserDescriptor.of(new NetworkParser(), Network.class);
    }

    @Override
    public @NotNull ArgumentParseResult<Network> parse(
            final @NotNull CommandContext<Source> context,
            final @NotNull CommandInput inputQueue) {
        final String input = inputQueue.readString();

        Network network = mgr.getFromName(input);

        if (network == null)
            return ArgumentParseResult.failure(new NetworkParseException(input, context));

        return ArgumentParseResult.success(network);
    }

    @Override
    public @NotNull List<@NotNull String> stringSuggestions(
            final @NotNull CommandContext<Source> commandContext,
            final @NotNull CommandInput input) {

        List<String> output = new ArrayList<>();

        if (commandContext.sender() instanceof Player) {
            for (Network network : mgr.withUser(((Player) commandContext.sender()).getUniqueId())) {
                output.add(network.name());
            }
        } else {
            for (Network network : mgr.getNetworks()) {
                output.add(network.name());
            }
        }

        return output;
    }


    /**
     * Network Parse Exception
     */
    public static final class NetworkParseException extends ParserException {

        private final String input;

        /**
         * Construct a new Network parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public NetworkParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    NetworkParser.class,
                    context,
                    Caption.of("argument.parse.failure.network"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;

//            if (!context.isSuggestions()) {
//                lang.message((Source) context.sender(), "invalidnetwork", input);
//            }
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public @NotNull String getInput() {
            return this.input;
        }
    }
}
