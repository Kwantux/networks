package de.kwantux.networks.commands;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.setting.ManagerSetting;

import java.util.UUID;

import static de.kwantux.networks.Main.*;
import static org.incendo.cloud.bukkit.parser.location.LocationParser.locationParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class TeleportCommand extends CommandHandler {

    public TeleportCommand(Main plugin, CommandManager<CommandSender> commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        cmd.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);

        cmd.command(cmd.commandBuilder("teleporttoworld", "tpw")
                .required("world", uuidParser())
                .required("location", locationParser())
                .senderType(Player.class)
                .handler(this::teleport)
                .permission("networks.teleport")
        );
    }

    private void teleport(CommandContext<Player> context) {
        World world = Bukkit.getWorld((UUID) context.get("world"));
        Location location = context.get("location");
        if (world == null) {
            lang.message(context.sender(), "teleport.world-invalid", context.get("world").toString());
            return;
        }
        context.sender().teleport(new Location(world, location.getX(), location.getY(), location.getZ()));
        lang.message(context.sender(), "teleport.success", world.getName(), ""+location.getX(), ""+location.getY(), ""+location.getZ());
    }


}
