package de.kwantux.networks.tests;

import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import de.kwantux.networks.commands.CommandHandler;
import de.kwantux.networks.component.component.InputContainer;
import de.kwantux.networks.component.component.MiscContainer;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.utils.BlockLocation;
import de.kwantux.networks.utils.ItemHash;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import java.util.List;

import static de.kwantux.networks.Main.crf;
import static de.kwantux.networks.Main.logger;
import static de.kwantux.networks.Main.mgr;
import static de.kwantux.networks.component.util.ComponentType.INPUT;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;


public class StressTest extends CommandHandler {

    public StressTest(Main plugin, PaperCommandManager<Source> commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("place")
                .literal("stress")
                .literal("simple")
                .permission("networks.data")
                .required("sizex", integerParser(1))
                .required("sizey", integerParser(1))
                .required("sizez", integerParser(1))
                .optional("outputs", integerParser(1))
                .senderType(PlayerSource.class)
                .handler(this::placeSimpleStressTest)
        );
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("place")
                .literal("stress")
                .literal("strict")
                .permission("networks.data")
                .required("sizex", integerParser(1))
                .required("sizey", integerParser(1))
                .required("sizez", integerParser(1))
                .optional("outputs", integerParser(1))
                .senderType(PlayerSource.class)
                .handler(this::placeStrictStressTest)
        );
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("place")
                .literal("oneperchunk")
                .permission("networks.data")
                .required("sizex", integerParser(1))
                .required("sizez", integerParser(1))
                .senderType(PlayerSource.class)
                .handler(this::placeOnePerChunk)
        );
        cmd.command(cmd.commandBuilder("networkstest", "ntest")
                .literal("delete")
                .permission("networks.data")
                .handler(this::deleteAll)
        );
    }

    private void placeSimpleStressTest(CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        org.bukkit.Location location = player.getLocation();
        int sizex = context.get("sizex");
        int sizey = context.get("sizey");
        int sizez = context.get("sizez");
        int outputs = context.getOrDefault("outputs", 1);
        int unitHeight = 2 + outputs;

        World world = location.getWorld();

        for (int x = 0; x < sizex; x++) {
            for (int z = 0; z < sizez; z++) {
                for (int y = 0; y < sizey*unitHeight; y+=unitHeight) {

                    int px = location.getBlockX() + x;
                    int pz = location.getBlockZ() + z;
                    int py = location.getBlockY() + y;

                    Block a = world.getBlockAt(px, py, pz);        a.setType(Material.BARREL); placedBlocks.add(a);
                    Block b = world.getBlockAt(px, py + 1, pz); b.setType(Material.HOPPER); placedBlocks.add(b);
                    Block c = world.getBlockAt(px, py + 2, pz); c.setType(Material.BARREL); placedBlocks.add(c);

                    Barrel barrel = (Barrel) c.getState();
                    barrel.getInventory().addItem(new ItemStack(Material.REDSTONE, 3));

                    String id = "test-s-" + px + "-" + py + "-" + pz;

                    mgr.create(id, player.getUniqueId());
                    Network network = mgr.getFromName(id);
                    mgr.createComponent(network, INPUT, new BlockLocation(px, py, pz, world.getUID()), null);
                    network.addComponent(new MiscContainer(new BlockLocation(px, py+2, pz, world.getUID()), network, 100));
                    for (int i = 1; i < outputs; i++) {
                        Block d = world.getBlockAt(px, py + 2 + i, pz); d.setType(Material.BARREL); placedBlocks.add(d);
                        network.addComponent(new MiscContainer(new BlockLocation(px, py+2+i, pz, world.getUID()), network, -100));
                    }

                }
            }
        }
    }

    private void placeStrictStressTest(CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        org.bukkit.Location location = player.getLocation();
        int sizex = context.get("sizex");
        int sizey = context.get("sizey");
        int sizez = context.get("sizez");
        int outputs = context.getOrDefault("outputs", 1);
        int unitHeight = 2 + outputs;

        World world = location.getWorld();
        ItemStack stack = crf.getNetworkWand(0);
        int[] filters = new int[]{ItemHash.strictHash(stack)};
        int[] emptyFilters = new int[0];

        for (int x = 0; x < sizex; x++) {
            for (int z = 0; z < sizez; z++) {
                for (int y = 0; y < sizey*unitHeight; y+=unitHeight) {

                    int px = location.getBlockX() + x;
                    int pz = location.getBlockZ() + z;
                    int py = location.getBlockY() + y;

                    Block a = world.getBlockAt(px, py, pz);        a.setType(Material.BARREL); placedBlocks.add(a);
                    Block b = world.getBlockAt(px, py + 1, pz); b.setType(Material.HOPPER); placedBlocks.add(b);
                    Block c = world.getBlockAt(px, py + 2, pz); c.setType(Material.BARREL); placedBlocks.add(c);

                    Barrel barrel = (Barrel) c.getState();
                    barrel.getInventory().addItem(stack.add(2));

                    String id = "test-strict-" + px + "-" + py + "-" + pz;

                    mgr.create(id, player.getUniqueId());
                    Network network = mgr.getFromName(id);
                    mgr.createComponent(network, INPUT, new BlockLocation(px, py, pz, world.getUID()), null);
                    network.addComponent(new SortingContainer(new BlockLocation(px, py+2, pz, world.getUID()), network, filters, 20));

                    for (int i = 1; i < outputs; i++) {
                        Block d = world.getBlockAt(px, py + 2 + i, pz); d.setType(Material.BARREL); placedBlocks.add(d);
                        network.addComponent(new SortingContainer(new BlockLocation(px, py+2+i, pz, world.getUID()), network, emptyFilters, 30));
                    }
                }
            }
        }
    }

    private void placeOnePerChunk(CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
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

                Block block = world.getBlockAt(px, y, pz);
                block.setType(Material.BARREL);
                placedBlocks.add(block);

                mgr.createComponent(network, InputContainer.type, new BlockLocation(px, y, pz, world.getUID()), null);
            }
        }
    }

    static List<Block> placedBlocks = new java.util.ArrayList<>();

    private void deleteAll(CommandContext<Source> context) {
        cleanup();
    }

    public static void cleanup() {
        logger.info("Cleaning up stress test networks and blocks... (This may take a while)");
        for (String network : new java.util.ArrayList<>(mgr.getNetworkIDs())) {
            if (network.startsWith("test-")) mgr.delete(network);
        }
        for (Block block : placedBlocks) {
            block.setType(Material.AIR);
        }
        placedBlocks.clear();
        logger.info("Cleanup complete");
    }
}
