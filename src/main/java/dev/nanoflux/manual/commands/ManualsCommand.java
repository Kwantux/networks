package dev.nanoflux.manual.commands;

import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.manual.Manual;
import dev.nanoflux.manual.ManualManager;
import dev.nanoflux.networks.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;


public class ManualsCommand extends CommandHandler {

    LanguageController lang;

    public ManualsCommand(Main plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("manuals", "manual", "read", "wiki")
                .handler(this::help)
        );
        commandManager.command(commandManager.commandBuilder("manuals", "manual", "read", "wiki")
                .literal("version")
                .handler(this::version)
        );
        commandManager.command(commandManager.commandBuilder("manuals", "manual", "read", "wiki")
               .literal("list")
               .handler(this::list)
        );
        commandManager.command(commandManager.commandBuilder("manuals", "manual", "read", "wiki")
                .literal("read")
                .argument(ManualArgument.of("id"))
                .handler(this::read)
        );
    }


    private void help(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        lang.message(sender, "manual.help");
    }

    private void version(CommandContext<CommandSender> context) {
        lang.message(context.getSender(), "manual.version", "1.0.0");
    }

    private void list(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Set<String> list = ManualManager.list.keySet();
        if (list.size() > 0) {
            lang.message(sender, "manual.list");
            for (String id : list) {
                sender.sendMessage(Component.text(id).clickEvent(ClickEvent.runCommand("/manuals read " + id)).hoverEvent(HoverEvent.showText(Component.text("Click to open manual"))));
            }
        }
        else {
            lang.message(sender, "manual.empty");
        }
    }

    private void read(CommandContext<CommandSender> context) {
        lang.message(context.getSender(), "manual.read", ((Manual) context.get("id")).getId());
        ((Manual) context.get("id")).show((Player) context.getSender());
    }


}
