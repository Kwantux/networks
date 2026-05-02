package de.kwantux.networks;

import de.kwantux.config.lang.LanguageController;
import de.kwantux.networks.commands.NetworksCommandManager;
import de.kwantux.networks.component.util.FilterTranslator;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.config.CraftingManager;
import de.kwantux.networks.event.*;
import de.kwantux.networks.utils.DoubleChestUtils;
import de.kwantux.networks.utils.FoliaUtils;
import de.kwantux.networks.utils.Metrics;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.data.Metric;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static de.kwantux.networks.utils.DevelopmentUtils.runInDevelopment;
import static de.kwantux.networks.utils.DevelopmentUtils.runInProduction;


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

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private BukkitMetrics metrics;

    @Override
    public void onEnable() {

        instance = this;

        runInDevelopment(() -> {
            getLogger().info("-- Running in development mode --");
        });

        runInProduction(() -> {
            this.metrics = BukkitMetrics.factory()
                    // Token for the plugin
                    .token("32ee6467575dea15ddc874e8766af069")

                    // Error tracker
                    .errorTracker(ERROR_TRACKER)

                    // Function to call when metrics data is submitted
                    // .onFlush(() -> resetCounters())

                    // Custom metrics
                    .addMetric(Metric.number("players_using_networks", () -> mgr.getPlayersUsingNetworksCount()))
                    .addMetric(Metric.number("total_networks", () -> mgr.getNetworks().size()))
                    .addMetric(Metric.number("total_components", () -> mgr.getTotalComponentCount()))
                    .addMetric(Metric.number("online_players_using_networks", () -> mgr.getOnlinePlayersUsingNetworksCount()))

                    // Create the metrics instance
                    .create(this);
            metrics.ready();
        });

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

        saveResource("lang/de.yml", true);
        saveResource("lang/en.yml", true);


        logger = getLogger();



        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        logger.info("Loading config files...");
        this.cfg = new Config(this);

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
        dcu = new DoubleChestUtils();
        crf = new CraftingManager(this);

        // bStats Metrics
        runInProduction(() -> {
            int pluginId = 18609;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new Metrics.SingleLineChart("total_networks", () ->
                mgr.getNetworks().size()
            ));
        });

        mgr.loadData();
        mgr.saveData();

        new ComponentDonateListener(this);
        new ComponentInstallListener(this);
        new BlockBreakListener(this);
        new WandListener(this);
        new PlayerJoinListener(this);
        new NoticeListener(this);
        new ClearFilterListener(this);

        if (FoliaUtils.folia) {
            logger.warning("Folia support on Networks is still in beta, please report any bugs.");
            for (int i : Config.ranges) {
                if (i > 1000) {
                    logger.warning("You are running Networks on Folia and enabled a maximum network range of more than 1000 blocks. Be aware that on Folia, you might not be able to transmit items that far.");
                    break;
                }
            }
        }

        if (Config.autoSaveInterval > 0)
            asyncScheduler.runAtFixedRate(this, (t) -> mgr.saveData(), Config.autoSaveInterval, Config.autoSaveInterval, TimeUnit.SECONDS);

        if (cfg.logoOnLaunch()) logger.info(startMessage);

    }

    @Override
    public void onDisable() {
        if (mgr != null) mgr.saveData();
        runInProduction(() ->
            metrics.shutdown()
        );
    }

    private final String startMessage =
    """


   _  __    __                  __          ____  ___
  / |/ /__ / /__    _____  ____/ /__ ___   |_  / <  /
 /    / -_) __/ |/|/ / _ \\/ __/  '_/(_-<  _/_ <_ / /
/_/|_/\\__/\\__/|__,__/\\___/_/ /_/\\_\\/___/ /____(_)_/
    """;

    private final String startMessage32 =
    """


   _  __    __                  __          ____   ___
  / |/ /__ / /__    _____  ____/ /__ ___   |_  /  |_  |
 /    / -_) __/ |/|/ / _ \\/ __/  '_/(_-<  _/_ <_ / __/
/_/|_/\\__/\\__/|__,__/\\___/_/ /_/\\_\\/___/ /____(_)____/
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
