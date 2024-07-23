package dev.nanoflux.networks;

import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.manual.Manual;
import dev.nanoflux.networks.commands.CommandManager;
import dev.nanoflux.networks.component.component.MiscContainer;
import dev.nanoflux.networks.component.component.SortingContainer;
import dev.nanoflux.networks.event.BlockBreakListener;
import dev.nanoflux.networks.event.BlockPlaceListener;
import dev.nanoflux.networks.event.ComponentListener;
import dev.nanoflux.networks.event.WandListener;
import dev.nanoflux.networks.event.PlayerJoinListener;
import dev.nanoflux.networks.utils.BlockLocation;
import dev.nanoflux.networks.utils.DoubleChestUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    // Variables
    public static Logger logger;

    public static Manager manager;
    public static Config config;
    public static CraftingManager crafting;
    public static DoubleChestUtils dcu;
    public static LanguageController lang;


    @Override
    public void onEnable() {

        // Create folders
        try {
            Files.createDirectories(Path.of(getDataFolder().getAbsolutePath(), "networks"));
            Files.createDirectories(Path.of(getDataFolder().getAbsolutePath(), "manuals"));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            this.config = new Config(this);
        }
        catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }


        lang = new LanguageController(this, config.getLanguage(), "en", "de");
        manager = new Manager(this);
        crafting = new CraftingManager(this);

        new Manual(this, "main", config.getLanguage());


        // bStats Metrics
        int pluginId = 18609;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_networks", () ->
            manager.getNetworks().size()
        ));

        try {
            new CommandManager(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        dcu = new DoubleChestUtils(manager);
        manager.loadData();

        new ComponentListener(this);
        new BlockPlaceListener(this, dcu);
        new BlockBreakListener(this, crafting, dcu);
        new WandListener(this, crafting, dcu);
        new PlayerJoinListener(this);

        if (FoliaUtils.folia) {
            logger.warning("Folia support on Networks is still in beta, please report any bugs.");
            for (int i : config.getMaxRanges()) {
                if (i > 1000) {
                    logger.warning("You are running Networks on Folia and enabled a maximum network range of more than 1000 blocks. Be aware that on Folia, you might not be able to transmit items that far.");
                    break;
                }
            }
        }

        if (config.logoOnLaunch()) logger.info(startMessage);

    }




    @Override
    public void onDisable() {
        // InventoryMenuManager.closeAll();
        if (manager != null) manager.saveData();
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
        return manager;
    }

    public Config getConfiguration() {
        return config;
    }


}
