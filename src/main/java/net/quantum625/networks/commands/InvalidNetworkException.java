package net.quantum625.networks.commands;

import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.ParserException;

public class InvalidNetworkException extends ParserException {

    public InvalidNetworkException(CommandContext context, String networkID) {
        super(NetworkArgument.class, context, Caption.of("Invalid network <%network>"), CaptionVariable.of("network", networkID));
    }
}
