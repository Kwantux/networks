package net.quantum625.networks;


import net.quantum625.config.lang.Language;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.networks.commands.CommandManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.inventory.InventoryMenuManager;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.quantum625.networks.listener.*;
import net.quantum625.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;


public final class Main extends JavaPlugin {

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

        try {
            this.config = new Config(this);
        }
        catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        // For Modrinth




        if (config.updateAllowed()) {
            Updater updater = new Updater(this, "1.0.0-SNAPSHOT-2a", "Networks", "KKr3r1PM");
            Updater.UpdateResult result = updater.update(Updater.ReleaseType.ALPHA, getFile());

            /* For Bukkit
            Updater updater = new Updater(this, 687035, this.getFile(), Updater.UpdateType.DEFAULT, true);
            Updater.UpdateResult result = updater.getResult();

            switch (result) {
                case SUCCESS:
                    Bukkit.getLogger().info("[Networks] Successfully updated plugin.");
                    Bukkit.getLogger().info("[Networks] It is recommended to restart the server now.");
                    break;
                case NO_UPDATE:
                    Bukkit.getLogger().info("[Networks] No update found.");
                    break;
                case DISABLED:
                    Bukkit.getLogger().info("[Networks] Updating was disabled in the configs.");
                    break;
                default:
                    Bukkit.getLogger().warning("[Networks] An unexpected error occurred while trying to update the plugin");
            }
            */
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


}
