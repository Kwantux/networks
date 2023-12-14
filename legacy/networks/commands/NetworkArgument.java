package quantum625.networks.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.quantum625.config.lang.LanguageController;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Manager;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class NetworkArgument<C> extends CommandArgument<C, Network> {

    static Manager net;
    static LanguageController lang;

    private NetworkArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
                    @NotNull List<@NotNull String>> suggestionsProvider,
            final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new NetworkArgument.NetworkParser<>(), defaultValue, Network.class, suggestionsProvider, defaultDescription);
        net = Main.getPlugin(Main.class).getNetworkManager();
        lang = Main.getPlugin(Main.class).getLanguage();
    }

    /**
     * Create a new {@link NetworkArgument.Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return new {@link NetworkArgument.Builder}
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public static <C> NetworkArgument.@NotNull Builder<C> builder(final @NotNull String name) {
        return new NetworkArgument.Builder<>(name);
    }


    /**
     * Create a new required command component
     *
     * @param name NetworkComponent name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Network> of(final @NotNull String name) {
        return NetworkArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name NetworkComponent name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Network> optional(final @NotNull String name) {
        return NetworkArgument.<C>builder(name).asOptional().build();
    }



    public static final class Builder<C> extends CommandArgument.Builder<C, Network> {

        private Builder(final @NotNull String name) {
            super(Network.class, name);
        }

        /**
         * Builder a new boolean component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull NetworkArgument<C> build() {
            return new NetworkArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }
    public static final class NetworkParser<C> implements ArgumentParser<C, Network> {

        @Override
        public @NotNull ArgumentParseResult<Network> parse(final @NotNull CommandContext<C> commandContext, final @NotNull Queue<@NotNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(NetworkArgument.NetworkParser.class, commandContext));
            }

            Network network = net.getFromID(input);

            if (network == null) {
                return ArgumentParseResult.failure(new NetworkParseException(input, commandContext));
            }

            inputQueue.remove();

            return ArgumentParseResult.success(network);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            if (commandContext.getSender() instanceof Player) {
                for (Network network : net.listFromUser(((Player) commandContext.getSender()).getUniqueId())) {
                    output.add(network.getID());
                }
            }
            else {
                for (Network network : net.listAll()) {
                    output.add(network.getID());
                }
            }

            return output;
        }
    }

    /**
     * Network parse exception
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
                    NetworkArgument.NetworkParser.class,
                    context,
                    Caption.of("argument.parse.failure.network"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;

            if (!context.isSuggestions()) {
                lang.message((CommandSender) context.getSender(), "invalidnetwork", input);
            }
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
