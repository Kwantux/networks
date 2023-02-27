package net.quantum625.networks;

//import net.quantum625.config.Configuration;
import net.quantum625.networks.commands.CommandListener;
import net.quantum625.networks.commands.LanguageModule;
import net.quantum625.networks.commands.TabCompleter;
import net.quantum625.networks.data.Config;
import net.quantum625.networks.data.CraftingManager;
import net.quantum625.networks.inventory.InventoryMenuManager;
import net.quantum625.networks.utils.DoubleChestDisconnecter;
import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.economy.Economy;
import net.quantum625.networks.listener.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;


public final class Main extends JavaPlugin {

    private File dataFolder;

    private Installer installer;
    private CommandListener commandListener;
    private TabCompleter tabCompleter;
    private NetworkManager net;
    private Config config;
    private CraftingManager crafting;
    private DoubleChestDisconnecter dcd;
    private Economy economy;
    private LanguageModule lang;
    private boolean economyState;
    private boolean error = false;

    @Override
    public void onEnable() {

        //Bukkit.getLogger().info("\n\n==================================\n   Networks Plugin has launched\n==================================\n");
        //Bukkit.getLogger().info(startMessage);


        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.dataFolder = this.getDataFolder();
        this.installer = new Installer(dataFolder, this);


        this.config = new Config(this, installer);
        economyState = config.getEconomyState();
        if (economyState) {
            economyState = setupEconomy();
            if (!economyState) {
                error = true;
                getServer().getPluginManager().disablePlugin(this);
            }
            config.initEconomy(economy);
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
            this.lang = new LanguageModule(this, installer, config.getLanguage());
            this.net = new NetworkManager(this.config, this.dataFolder, this.lang);
            this.crafting = new CraftingManager(this.dataFolder, config, lang);

            this.commandListener = new CommandListener(net, dataFolder, lang, config, economy);
            this.tabCompleter = new TabCompleter(net, config);

            getCommand("network").setExecutor(commandListener);
            getCommand("network").setTabCompleter(tabCompleter);

            this.dcd = new DoubleChestDisconnecter(net);

            this.getServer().getPluginManager().registerEvents(new AutoSave(net), this);
            this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(net, config, dcd, lang), this);
            this.getServer().getPluginManager().registerEvents(new ExplosionListener(config, lang, net), this);
            this.getServer().getPluginManager().registerEvents(new RightClickEventListener(net, lang, config), this);
            this.getServer().getPluginManager().registerEvents(new InventoryOpenEventListener(net, lang, config), this);
            this.getServer().getPluginManager().registerEvents(new InventoryCloseEventListener(net), this);
            this.getServer().getPluginManager().registerEvents(new ItemTransportEventListener(net, config), this);
            this.getServer().getPluginManager().registerEvents(new HopperCollectEventListener(net), this);
            this.getServer().getPluginManager().registerEvents(new BlockPlaceEventListener(net, config, dcd, lang), this);
            this.getServer().getPluginManager().registerEvents(new NetworkWandListener(config, net, lang, crafting), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
            this.getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);

            net.loadData();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("[Networks] Vault plugin is not installed, Economy feature will be disabled!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (getServer().getServicesManager() == null) {
            Bukkit.getLogger().warning("[Networks] No service manager found");
            return false;
        }

        if (rsp == null) {
            Bukkit.getLogger().warning("[Networks] Failed to register Economy Service Provider! Do you have a supported economy plugin installed?");
            Bukkit.getLogger().warning("[Networks] See https://github.com/Quantum625/networks/wiki/Supported-Plugins for more information.");
            return false;
        }
        economy = rsp.getProvider();
        if (economy == null) {
            Bukkit.getLogger().warning("[Networks] Failed to enable economy, economy provider is null");
            return false;
        }
        Bukkit.getLogger().info("[Networks] Vault successfully registered");
        return true;
    }


    @Override
    public void onDisable() {
        if (!error) {
            InventoryMenuManager.closeAll();
            net.saveData();
        }
        //Bukkit.getLogger().info("\n\n==================================\n   Networks Plugin was shut down\n==================================\n");
    }

    private String startMessage = "\n" +
            "===========================================================================\n\n" +
            "            __   _                      _                    _   ___  \n" +
            "       /\\  / /__| |___      _____  _ __| | _____    __   __ / | / _ \\ \n" +
            "      /  \\/ / _ \\ __\\ \\ /\\ / / _ \\| '__| |/ / __|   \\ \\ / / | || | | |\n" +
            "     / /\\  /  __/ |_ \\ V  V / (_) | |  |   <\\__ \\    \\ V /  | || |_| |\n" +
            "    /_/  \\/ \\___|\\__| \\_/\\_/ \\___/|_|  |_|\\_\\___/     \\_/   |_(_)___/ \n" +
            "                                                                  \n"+
            "===========================================================================\n";
}
