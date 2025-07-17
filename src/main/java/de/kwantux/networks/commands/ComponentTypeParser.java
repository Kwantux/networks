package de.kwantux.networks.commands;

import de.kwantux.networks.component.util.ComponentType;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public final class ComponentTypeParser implements ArgumentParser<CommandSender, ComponentType>, BlockingSuggestionProvider.Strings<CommandSender> {


    public static @NonNull ParserDescriptor<CommandSender, ComponentType> componentTypeParser(boolean mustBePersistentComponentType) {
        return ParserDescriptor.of(new ComponentTypeParser(mustBePersistentComponentType), ComponentType.class);
    }

    private boolean mustBePersistentComponentType;

    public ComponentTypeParser(boolean mustBePhysicalComponentType) {
        this.mustBePersistentComponentType = mustBePhysicalComponentType;
    }

    @Override
    public @NotNull ArgumentParseResult<ComponentType> parse(
            final @NotNull CommandContext<CommandSender> context,
            final @NotNull CommandInput inputQueue) {
        final String input = inputQueue.readString();
        ComponentType componentType = ComponentType.get(input);

        if (componentType == null)
            return ArgumentParseResult.failure(new ComponentTypeParseException(input, context));

        if (mustBePersistentComponentType && !componentType.persistent)
            return ArgumentParseResult.failure(new ComponentTypeParseException(input, context));

        return ArgumentParseResult.success(componentType);
    }

    @Override
    public @NotNull List<@NotNull String> stringSuggestions(
            final @NotNull CommandContext<CommandSender> commandContext,
            final @NotNull CommandInput input) {

        List<String> output = new ArrayList<>();

        for (ComponentType componentType : ComponentType.types.values()) {
            if (!mustBePersistentComponentType || componentType.persistent) output.add(componentType.tag);
        }
        return output;
    }


    /**
     * ComponentType Parse Exception
     */
    public static final class ComponentTypeParseException extends ParserException {

        private final String input;

        /**
         * Construct a new ComponentType parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public ComponentTypeParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    ComponentTypeParser.class,
                    context,
                    Caption.of("argument.parse.failure.componenttype"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;

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
