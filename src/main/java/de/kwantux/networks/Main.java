package de.kwantux.networks;

import de.kwantux.networks.commands.NetworksCommandManager;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.utils.DoubleChestUtils;
import de.kwantux.networks.utils.FoliaUtils;
import de.kwantux.networks.utils.Metrics;
import de.kwantux.config.lang.LanguageController;
import de.kwantux.manual.Manual;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.config.CraftingManager;
import de.kwantux.networks.event.BlockBreakListener;
import de.kwantux.networks.event.BlockPlaceListener;
import de.kwantux.networks.event.ComponentListener;
import de.kwantux.networks.event.WandListener;
import de.kwantux.networks.event.PlayerJoinListener;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    public static Main instance;

    public static Logger logger;

    public static RegionScheduler regionScheduler;
    public static GlobalRegionScheduler globalRegionScheduler;
    public static AsyncScheduler asyncScheduler;

    public static Manager mgr;
    public static Config cfg;
    public static CraftingManager crf;
    public static DoubleChestUtils dcu;
    public static LanguageController lang;


    @Override
    public void onEnable() {

        instance = this;


        // Create folders
        try {
            Files.createDirectories(Path.of(getDataFolder().getAbsolutePath(), "networks"));
            Files.createDirectories(Path.of(getDataFolder().getAbsolutePath(), "manuals"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Files.exists(Path.of(getDataFolder().getAbsolutePath(), "networks.conf"))) {
            try {
                Files.move(Path.of(getDataFolder().getAbsolutePath(), "networks.conf"), Path.of(getDataFolder().getAbsolutePath(), "general.conf"));
            } catch (IOException _ignore) {}
        }

        saveResource("README.md", true);

        saveResource("manuals/main/de.yml", true);
        saveResource("manuals/main/en.yml", true);


        logger = getLogger();



        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            logger.info("Loading config files...");
            this.cfg = new Config(this);
        }
        catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        lang = new LanguageController(this, cfg.getLanguage(), "en", "de");



        new NetworksCommandManager(this);

        regionScheduler = getServer().getRegionScheduler();
        globalRegionScheduler = getServer().getGlobalRegionScheduler();
        asyncScheduler = getServer().getAsyncScheduler();

        try {
            FilterTranslator.load(Path.of(getDataFolder().getAbsolutePath(), "filters.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mgr = new Manager(this);
        crf = new CraftingManager(this);

        new Manual(this, "main", cfg.getLanguage());


        // bStats Metrics
        int pluginId = 18609;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_networks", () ->
            mgr.getNetworks().size()
        ));

        dcu = new DoubleChestUtils(mgr);
        mgr.loadData();

        new ComponentListener(this);
        new BlockPlaceListener(this, dcu);
        new BlockBreakListener(this, dcu);
        new WandListener(this, crf, dcu);
        new PlayerJoinListener(this);

        if (FoliaUtils.folia) {
            logger.warning("Folia support on Networks is still in beta, please report any bugs.");
            for (int i : cfg.getMaxRanges()) {
                if (i > 1000) {
                    logger.warning("You are running Networks on Folia and enabled a maximum network range of more than 1000 blocks. Be aware that on Folia, you might not be able to transmit items that far.");
                    break;
                }
            }
        }

        if (cfg.logoOnLaunch()) logger.info(startMessage);

    }




    @Override
    public void onDisable() {
        // InventoryMenuManager.closeAll();
        if (mgr != null) mgr.saveData();
    }


    private final String largeStartMessage =
    """
        _   __     __                      __                  _____  ____ 
       / | / /__  / /__      ______  _____/ /_______     _   _|__  / / __ \\
      /  |/ / _ \\/ __/ | /| / / __ \\/ ___/ //_/ ___/    | | / //_ < / / / /
     / /|  /  __/ /_ | |/ |/ / /_/ / /  / ,< (__  )     | |/ /__/ // /_/ / 
    /_/ |_/\\___/\\__/ |__/|__/\\____/_/  /_/|_/____/      |___/____(_)____/  
                                                                         
    """;

    private final String startMessage =
    """
       
       _  __    __                  __                ____  ___\s
      / |/ /__ / /__    _____  ____/ /__ ___    _  __|_  / / _ \\
     /    / -_) __/ |/|/ / _ \\/ __/  '_/(_-<   | |/ //_ <_/ // /
    /_/|_/\\__/\\__/|__,__/\\___/_/ /_/\\_\\/___/   |___/____(_)___/\s
    """;

    public LanguageController getLanguage() {
        return lang;
    }

    public Manager getNetworkManager() {
        return mgr;
    }

    public Config getConfiguration() {
        return cfg;
    }


}
