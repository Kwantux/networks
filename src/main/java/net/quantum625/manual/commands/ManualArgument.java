package net.quantum625.manual.commands;

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
import net.quantum625.manual.Manual;
import net.quantum625.manual.ManualManager;
import net.quantum625.networks.Main;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class ManualArgument<C> extends CommandArgument<C, Manual> {
    static LanguageController lang;

    private ManualArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
                    @NotNull List<@NotNull String>> suggestionsProvider,
            final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new ManualParser<>(), defaultValue, Manual.class, suggestionsProvider, defaultDescription);
        lang = Main.getPlugin(Main.class).getLanguage();
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return new {@link Builder}
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public static @NotNull <C> Builder<C> builder(final @NotNull String name) {
        return new Builder<>(name);
    }


    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Manual> of(final @NotNull String name) {
        return ManualArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Manual> optional(final @NotNull String name) {
        return ManualArgument.<C>builder(name).asOptional().build();
    }



    public static final class Builder<C> extends CommandArgument.Builder<C, Manual> {

        private Builder(final @NotNull String name) {
            super(Manual.class, name);
        }

        /**
         * Builder a new boolean component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull ManualArgument<C> build() {
            return new ManualArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }
    public static final class ManualParser<C> implements ArgumentParser<C, Manual> {

        @Override
        public @NotNull ArgumentParseResult<Manual> parse(final @NotNull CommandContext<C> commandContext, final @NotNull Queue<@NotNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(ManualParser.class, commandContext));
            }

            Manual manual = ManualManager.list.get(input);

            if (manual == null) {
                return ArgumentParseResult.failure(new ManualParseException(input, commandContext));
            }

            inputQueue.remove();

            return ArgumentParseResult.success(manual);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            return ManualManager.list.keySet().stream().toList();
        }
    }

    /**
     * Manual parse exception
     */
    public static final class ManualParseException extends ParserException {

        private final String input;

        /**
         * Construct a new Manual parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public ManualParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    ManualParser.class,
                    context,
                    Caption.of("argument.parse.failure.manual"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;

            if (!context.isSuggestions()) {
                lang.message((CommandSender) context.getSender(), "invalidmanual", input);
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
