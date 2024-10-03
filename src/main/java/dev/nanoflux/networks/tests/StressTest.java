package dev.nanoflux.networks.tests;

import dev.nanoflux.networks.commands.CommandHandler;
import dev.nanoflux.networks.commands.NetworksCommandManager;
import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.config.Config;
import dev.nanoflux.networks.Manager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.paper.PaperCommandManager;


public class StressTest extends CommandHandler {

    LanguageController lang;
    Manager manager;
    Config config;

    public StressTest(Main plugin, PaperCommandManager<CommandSender> commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
        manager = plugin.getNetworkManager();
        config = plugin.getConfiguration();
    }

    @Override
    public void register() {
//        cmd.command(cmd.commandBuilder("networks", "network", "net")
//                .literal("test")
//                .literal("place")
//                .literal("simple")
//                .permission("networks.data")
//                .argument(IntegerArgument.of("sizex"))
//                .argument(IntegerArgument.of("sizey"))
//                .argument(IntegerArgument.of("sizez"))
//                .senderType(Player.class)
//                .handler(this::placeSimple)
//        );
//        cmd.command(cmd.commandBuilder("networks", "network", "net")
//                .literal("test")
//                .literal("place")
//                .literal("multiple")
//                .permission("networks.data")
//                .argument(IntegerArgument.of("sizex"))
//                .argument(IntegerArgument.of("sizey"))
//                .argument(IntegerArgument.of("sizez"))
//                .argument(IntegerArgument.of("sizen"))
//                .senderType(Player.class)
//                .handler(this::placeMultiple)
//        );
    }

//    private void placeSimple(CommandContext<CommandSender> context) {
//        Player player = (Player) context.getSender();
//        org.bukkit.Location location = player.getLocation();
//        int sizex = context.get("sizex");
//        int sizey = context.get("sizey");
//        int sizez = context.get("sizez");
//
//        World world = location.getWorld();
//
//        for (int x = 0; x < sizex; x++) {
//            for (int z = 0; z < sizez; z++) {
//                for (int y = 0; y < sizey*3; y+=3) {
//
//                    int px = location.getBlockX() + x;
//                    int pz = location.getBlockZ() + z;
//                    int py = location.getBlockY() + y;
//
//                    world.getBlockAt(px, py, pz).setType(Material.BARREL);
//                    world.getBlockAt(px, py + 1, pz).setType(Material.HOPPER);
//                    world.getBlockAt(px, py + 2, pz).setType(Material.BARREL);
//                    Barrel barrel = (Barrel) world.getBlockAt(px, py+2, pz).getState();
//                    barrel.getInventory().addItem(new ItemStack(Material.REDSTONE, 3));
//
//                    String id = "t-s-" + px + "-" + py + "-" + pz;
//
//                    mgr.create(id, player.getUniqueId());
//                    Network network = mgr.getFromName(id);
//                    mgr.createComponent(network, Material.BARREL, InputContainer.type, new BlockLocation(px, py, pz, world.getUID()), null);
//                    mgr.createComponent(network, Material.BARREL, MiscContainer.type, new BlockLocation(px, py+2, pz, world.getUID()), null);
//
//                }
//            }
//        }
//    }
//
//    private void placeMultiple(CommandContext<CommandSender> context) {
//        Player player = (Player) context.getSender();
//        org.bukkit.Location location = player.getLocation();
//        int sizex = context.get("sizex");
//        int sizey = context.get("sizey");
//        int sizez = context.get("sizez");
//        int sizen = context.get("sizen");
//
//        World world = location.getWorld();
//
//        for (int x = 0; x < sizex; x++) {
//            for (int z = 0; z < sizez; z++) {
//                for (int y = 0; y < sizey*(sizen+2); y+=(sizen+2)) {
//
//                    int px = location.getBlockX() + x;
//                    int pz = location.getBlockZ() + z;
//                    int py = location.getBlockY() + y;
//
//                    world.getBlockAt(px, py, pz).setType(Material.BARREL);
//                    world.getBlockAt(px, py + 1, pz).setType(Material.HOPPER);
//                    Hopper barrel = (Hopper) world.getBlockAt(px, py+1, pz).getState();
//                    barrel.getInventory().addItem(new ItemStack(Material.REDSTONE, 3));
//
//                    String id = "t-s-" + px + "-" + py + "-" + pz;
//
//                    mgr.create(id, player.getUniqueId());
//                    Network network = mgr.getFromName(id);
//                    mgr.createComponent(network, Material.BARREL, InputContainer.type, new BlockLocation(px, py, pz, world.getUID()), null);
//
//                    for (int n = 2; n < sizen; n++) {
//
//                        world.getBlockAt(px, py + n, pz).setType(Material.BARREL);
//
//                        mgr.createComponent(network, Material.BARREL, MiscContainer.type, new BlockLocation(px, py+n, pz, world.getUID()), null);
//
//                    }
//
//                }
//            }
//        }
//    }

}
