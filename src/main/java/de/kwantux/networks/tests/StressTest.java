package de.kwantux.networks.tests;

import de.kwantux.config.lang.LanguageController;
import de.kwantux.networks.Main;
import de.kwantux.networks.Manager;
import de.kwantux.networks.Network;
import de.kwantux.networks.commands.CommandHandler;
import de.kwantux.networks.component.component.InputContainer;
import de.kwantux.networks.component.component.MiscContainer;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.utils.BlockLocation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import static de.kwantux.networks.Main.mgr;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;


public class StressTest extends CommandHandler {

    LanguageController lang;
    Manager manager;
    Config config;

    public StressTest(Main plugin, LegacyPaperCommandManager<CommandSender> commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
        manager = plugin.getNetworkManager();
        config = plugin.getConfiguration();
    }

    @Override
    public void register() {
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("place")
                .literal("stress")
                .permission("networks.data")
                .required("sizex", integerParser(Integer.MIN_VALUE))
                .required("sizey", integerParser(Integer.MIN_VALUE))
                .required("sizez", integerParser(Integer.MIN_VALUE))
                .senderType(Player.class)
                .handler(this::placeSimpleStressTest)
        );
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("place")
                .literal("oneperchunk")
                .permission("networks.data")
                .required("sizex", integerParser(Integer.MIN_VALUE))
                .required("sizez", integerParser(Integer.MIN_VALUE))
                .senderType(Player.class)
                .handler(this::placeOnePerChunk)
        );
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("delete")
                .permission("networks.data")
                .handler(this::deleteAll)
        );
    }

    private void placeSimpleStressTest(CommandContext<Player> context) {
        Player player = context.sender();
        org.bukkit.Location location = player.getLocation();
        int sizex = context.get("sizex");
        int sizey = context.get("sizey");
        int sizez = context.get("sizez");

        World world = location.getWorld();

        for (int x = 0; x < sizex; x++) {
            for (int z = 0; z < sizez; z++) {
                for (int y = 0; y < sizey*3; y+=3) {

                    int px = location.getBlockX() + x;
                    int pz = location.getBlockZ() + z;
                    int py = location.getBlockY() + y;

                    world.getBlockAt(px, py, pz).setType(Material.BARREL);
                    world.getBlockAt(px, py + 1, pz).setType(Material.HOPPER);
                    world.getBlockAt(px, py + 2, pz).setType(Material.BARREL);
                    Barrel barrel = (Barrel) world.getBlockAt(px, py+2, pz).getState();
                    barrel.getInventory().addItem(new ItemStack(Material.REDSTONE, 3));

                    String id = "test-s-" + px + "-" + py + "-" + pz;

                    mgr.create(id, player.getUniqueId());
                    Network network = mgr.getFromName(id);
                    mgr.createComponent(network, InputContainer.type, new BlockLocation(px, py, pz, world.getUID()), null);
                    mgr.createComponent(network, MiscContainer.type, new BlockLocation(px, py+2, pz, world.getUID()), null);

                }
            }
        }
    }

    private void placeOnePerChunk(CommandContext<Player> context) {
        Player player = context.sender();
        org.bukkit.Location location = player.getLocation();
        int sizex = context.get("sizex");
        int sizez = context.get("sizez");

        int y = location.getBlockY();

        World world = location.getWorld();
        String id = "test-opc-" + sizex + "-" + sizex + "-" + System.currentTimeMillis();
        mgr.create(id, player.getUniqueId());
        Network network = mgr.getFromName(id);

        for (int x = 0; x < sizex; x++) {
            for (int z = 0; z < sizez; z++) {

                int px = location.getBlockX() + x*16;
                int pz = location.getBlockZ() + z*16;

                world.getBlockAt(px, y, pz).setType(Material.BARREL);
                mgr.createComponent(network, InputContainer.type, new BlockLocation(px, y, pz, world.getUID()), null);
            }
        }
    }

    private void deleteAll(CommandContext<CommandSender> context) {
        for (String network : new java.util.ArrayList<>(mgr.getNetworkIDs())) {
            if (network.startsWith("test-")) mgr.delete(network);
        }
    }
}
