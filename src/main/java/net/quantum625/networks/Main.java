package net.quantum625.networks;



import net.quantum625.config.ConfigurationManager;
import net.quantum625.updater.Updater;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.commands.CommandManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.inventory.InventoryMenuManager;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.listener.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.logging.Logger;


import static net.gravitydevelopment.updater.Updater.UpdateType.DEFAULT;



public final class Main extends JavaPlugin {

    // CONSTANTS:
    public static boolean forceDisableUpdates = false;
    public static PublishingPlatform platform = PublishingPlatform.BUKKIT;


    // Variables
    private Logger logger;
    private NetworkManager net;
    private Config config;
    private CraftingManager crafting;
    private DoubleChestDisconnecter dcd;
    private LanguageController lang;
    private boolean error = false;

    @Override
    public void onEnable() {

        saveResource("README.md", true);

        logger = getLogger();

        logger.info(startMessage);


        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        ConfigurationManager.register(this);

        try {
            this.config = new Config(this);
        }
        catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }



        if (config.updateAllowed()) {
            if (platform.equals(PublishingPlatform.BUKKIT)) {

                // Bukkit downloader

                logger.info("[Updater] Checking for updates...");
                logger.info("[Updater] If you like to disable this updater, open networks.conf and set 'autoUpdate' to false");

                net.gravitydevelopment.updater.Updater updater = new net.gravitydevelopment.updater.Updater(this, 687035, this.getFile(), DEFAULT, true);
                net.gravitydevelopment.updater.Updater.UpdateResult result = updater.getResult();

                switch (result) {
                    case SUCCESS:
                        getLogger().info("[Updater] Successfully updated plugin.");
                        getLogger().info("[Updater] It is recommended to restart the server now.");
                        break;
                    case NO_UPDATE:
                        getLogger().info("[Updater] No update found.");
                        break;
                    case DISABLED:
                        getLogger().info("[Updater] Updating was disabled in the configs.");
                        break;
                    default:
                        getLogger().warning("[Updater] An unexpected error occurred while trying to update the plugin");
                }
            }

            if (platform.equals(PublishingPlatform.MODRINTH) || platform.equals(PublishingPlatform.GITHUB)) {

                // Modrinth downloader

                Updater updater = new Updater(this, "2.0.0", "Networks", "KKr3r1PM", true);
                Updater.UpdateResult result = updater.update(Updater.ReleaseType.STABLE, getFile());
                logger.info("[PluginUpdater] Update Result: " + result.toString());
            }

            if (platform.equals(PublishingPlatform.HANGAR)) {
                //TODO: Implement this
            }

            if (platform.equals(PublishingPlatform.SPIGOT)) {
                //TODO: Implement this
            }


        }

        else {
            // Does not install the update, only checks for the version
            Updater updater = new net.quantum625.updater.Updater(this, "2.0.0", "Networks", "KKr3r1PM", false);
            Updater.LinkResult linkResult = updater.getLink(Updater.ReleaseType.STABLE);
            if (linkResult.wasSuccessful()) {
                logger.info("[PluginUpdater] Version " + linkResult.getVersion() + " of Networks is now available!");
                logger.info("[PluginUpdater] Download on Modrinth:  https://modrinth.com/plugin/networks");
                logger.info("[PluginUpdater] Download on Bukkit:    https://curseforge.com/minecraft/bukkit-plugins/networks");
            }
        }


        if (!error) {
            this.lang = new LanguageController(this, config.getLanguage(), "en", "de");
            this.net = new NetworkManager(this);
            this.crafting = new CraftingManager(this);

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


            this.dcd = new DoubleChestDisconnecter(net);

            this.getServer().getPluginManager().registerEvents(new AutoSave(this), this);
            this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(this, crafting, dcd), this);
            this.getServer().getPluginManager().registerEvents(new ExplosionListener(this, crafting), this);
            this.getServer().getPluginManager().registerEvents(new InventoryOpenEventListener(this), this);
            this.getServer().getPluginManager().registerEvents(new InventoryCloseEventListener(this), this);
            this.getServer().getPluginManager().registerEvents(new ItemTransportEventListener(this), this);
            this.getServer().getPluginManager().registerEvents(new HopperCollectEventListener(this), this);
            this.getServer().getPluginManager().registerEvents(new BlockPlaceEventListener(this, dcd), this);
            this.getServer().getPluginManager().registerEvents(new NetworkWandListener(this, crafting), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
            this.getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);

            net.loadData();
        }
    }




    @Override
    public void onDisable() {
        if (!error) {
            InventoryMenuManager.closeAll();
            net.saveData();
        }
    }

    private String startMessage =
            "\n          _   __     __                      __                  ___    ____ "+
            "\n         / | / /__  / /__      ______  _____/ /_______     _   _|__ \\  / __ \\"+
            "\n        /  |/ / _ \\/ __/ | /| / / __ \\/ ___/ //_/ ___/    | | / /_/ / / / / /"+
            "\n       / /|  /  __/ /_ | |/ |/ / /_/ / /  / ,< (__  )     | |/ / __/_/ /_/ /"+
            "\n      /_/ |_/\\___/\\__/ |__/|__/\\____/_/  /_/|_/____/      |___/____(_)____/"+
            "\n";

    public LanguageController getLanguage() {
        return lang;
    }

    public NetworkManager getNetworkManager() {
        return net;
    }

    public Config getConfiguration() {
        return config;
    }


    public enum PublishingPlatform {
        MODRINTH,
        BUKKIT,
        SPIGOT,
        HANGAR,
        GITHUB,
        OTHER;
    }


}
