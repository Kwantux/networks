package de.kwantux.networks.commands;

import de.kwantux.networks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.standard.StringArrayParser;
import org.incendo.cloud.setting.ManagerSetting;

import java.util.UUID;

import static de.kwantux.networks.Main.lang;
import static org.incendo.cloud.bukkit.parser.location.LocationParser.locationParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class TeleportCommand extends CommandHandler {

    public TeleportCommand(Main plugin, PaperCommandManager<Source> commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        cmd.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);

        cmd.command(cmd.commandBuilder("teleporttoworld")
                .required("world", uuidParser())
                .required("location", locationParser())
                .senderType(PlayerSource.class)
                .handler(this::teleport)
                .permission("networks.teleport")
        );

        cmd.command(cmd.commandBuilder("runcmd")
                        .required("command", StringArrayParser.stringArrayParser())
                        .handler(this::runCommandIfSenderIsPlayer)
                        .permission("")
        );
    }

    private void teleport(CommandContext<PlayerSource> context) {
        World world = Bukkit.getWorld((UUID) context.get("world"));
        Location location = context.get("location");
        if (world == null) {
            lang.message(context.sender(), "teleport.world-invalid", context.get("world").toString());
            return;
        }
        context.sender().source().teleport(new Location(world, location.getX(), location.getY(), location.getZ()));
        lang.message(context.sender(), "teleport.success", world.getName(), ""+location.getX(), ""+location.getY(), ""+location.getZ());
    }

    private void runCommandIfSenderIsPlayer(CommandContext<Source> context) {
        String[] args = context.get("command");
        String command = String.join(" ", args);

        if (context.sender().source() instanceof Player player) {
            player.performCommand(command);
        }
    }


}
