package net.quantum625.networks;

import net.quantum625.config.lang.LanguageController;
import net.quantum625.manual.Manual;
import net.quantum625.networks.commands.CommandManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.inventory.InventoryMenuManager;
import net.quantum625.networks.listener.AutoSave;
import net.quantum625.networks.listener.BlockBreakEventListener;
import net.quantum625.networks.listener.BlockPlaceEventListener;
import net.quantum625.networks.listener.ExplosionListener;
import net.quantum625.networks.listener.HopperCollectEventListener;
import net.quantum625.networks.listener.InventoryCloseEventListener;
import net.quantum625.networks.listener.InventoryMenuListener;
import net.quantum625.networks.listener.InventoryOpenEventListener;
import net.quantum625.networks.listener.ItemTransportEventListener;
import net.quantum625.networks.listener.NetworkWandListener;
import net.quantum625.networks.listener.PlayerJoinEventListener;
import net.quantum625.networks.listener.RightClickEventListener;
import net.quantum625.networks.utils.DoubleChestUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    private final String startMessage =
            "\n        _   __     __                      __                ___    ___" +
                    "\n       / | / /__  / /__      ______  _____/ /_______   _   _|__ \\ /_  /" +
                    "\n      /  |/ / _ \\/ __/ | /| / / __ \\/ ___/ //_/ ___/  | | / /_/ /  / / " +
                    "\n     / /|  /  __/ /_ | |/ |/ / /_/ / /  / ,< (__  )   | |/ / __/_ / /  " +
                    "\n    /_/ |_/\\___/\\__/ |__/|__/\\____/_/  /_/|_/____/    |___/____(_)_/   " +
                    "\n";
    // Variables
    private Logger logger;
    private NetworkManager net = null;
    private Config config;
    private CraftingManager crafting;
    private DoubleChestUtils dcu;
    private LanguageController lang;

    public static Plugin getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onEnable() {
        boolean isPaper = hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
        if (!isPaper) {
            getLogger().severe("This plugin requires to be run on a Paper or Paper based server software.");
            throw new RuntimeException("Unsupported Server Software");
        }

        // Clear up old config files
        new File(getDataFolder(), "recipes.yml").delete();
        new File(getDataFolder(), "config.yml").delete();

        saveResource("README.md", true);

        saveResource("manuals/main/de.yml", true);
        saveResource("manuals/main/en.yml", true);


        logger = getLogger();


        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            logger.info("Loading config files...");
            this.config = new Config(this);
        } catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }


        this.lang = new LanguageController(this, config.getLanguage(), "en", "de");
        this.net = new NetworkManager(this);
        this.crafting = new CraftingManager(this);

        new Manual(this, "main", config.getLanguage());

        try {
            new net.quantum625.manual.commands.CommandManager(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // bStats Metrics
        int pluginId = 18609;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_networks", () ->
                net.listAll().size()
        ));

        try {
            new CommandManager(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        this.dcu = new DoubleChestUtils(net);

        this.getServer().getPluginManager().registerEvents(new AutoSave(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(this, crafting, dcu), this);
        this.getServer().getPluginManager().registerEvents(new ExplosionListener(this, crafting), this);
        this.getServer().getPluginManager().registerEvents(new InventoryCloseEventListener(this, dcu), this);
        this.getServer().getPluginManager().registerEvents(new ItemTransportEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new HopperCollectEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceEventListener(this, dcu), this);
        this.getServer().getPluginManager().registerEvents(new NetworkWandListener(this, crafting, dcu), this);
        this.getServer().getPluginManager().registerEvents(new RightClickEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);

        if (config.noticeEnabled())
            this.getServer().getPluginManager().registerEvents(new InventoryOpenEventListener(this), this);

        net.loadData();

        if (config.logoOnLaunch()) logger.info(startMessage);
    }

    @Override
    public void onDisable() {
        InventoryMenuManager.closeAll();
        if (net != null) net.saveData();
    }

    public LanguageController getLanguage() {
        return lang;
    }

    public NetworkManager getNetworkManager() {
        return net;
    }

    public Config getConfiguration() {
        return config;
    }


}
