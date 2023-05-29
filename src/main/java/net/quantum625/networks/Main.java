package net.quantum625.networks;


import net.quantum625.config.lang.Language;
import net.quantum625.networks.commands.CommandManager;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.inventory.InventoryMenuManager;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.gravitydevelopment.updater.Updater;
import net.quantum625.networks.listener.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;


public final class Main extends JavaPlugin {

    private Logger logger;

    private File dataFolder;

    private Installer installer;

    private NetworkManager net;
    private Config config;
    private CraftingManager crafting;
    private DoubleChestDisconnecter dcd;
    private Language lang;
    private boolean error = false;

    @Override
    public void onEnable() {



        logger = getLogger();

        logger.info(startMessage);


        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.dataFolder = this.getDataFolder();
        this.installer = new Installer(dataFolder, this);

        try {
            this.config = new Config(this);
        }
        catch (SerializationException e) {
            logger.severe("Unable to load config, shutting down plugin…");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        if (config.updateAllowed()) {
            Bukkit.getLogger().info("[Networks] Checking for updates...");
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
        }




        if (!error) {
            try {
                this.lang = new Language(this, config.getLanguage());
            } catch (SerializationException e) {
                logger.severe("Language file unable to load");
                e.printStackTrace();
            }
            this.net = new NetworkManager(this.config, this.dataFolder, this.lang);
            this.crafting = new CraftingManager(this, config, lang);

            net.add("test", UUID.randomUUID());

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

            this.getServer().getPluginManager().registerEvents(new AutoSave(net), this);
            this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(net, config, dcd, lang), this);
            this.getServer().getPluginManager().registerEvents(new ExplosionListener(config, lang, net, crafting), this);
            this.getServer().getPluginManager().registerEvents(new RightClickEventListener(net, lang, config), this);
            this.getServer().getPluginManager().registerEvents(new InventoryOpenEventListener(net), this);
            this.getServer().getPluginManager().registerEvents(new InventoryCloseEventListener(net), this);
            this.getServer().getPluginManager().registerEvents(new ItemTransportEventListener(net, config), this);
            this.getServer().getPluginManager().registerEvents(new HopperCollectEventListener(net), this);
            this.getServer().getPluginManager().registerEvents(new BlockPlaceEventListener(net, config, dcd, lang), this);
            this.getServer().getPluginManager().registerEvents(new NetworkWandListener(config, net, lang, crafting), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(config), this);
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

    public Language getLanguage() {
        return lang;
    }
}
