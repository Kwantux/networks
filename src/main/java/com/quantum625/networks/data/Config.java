package com.quantum625.networks.data;

import com.quantum625.networks.Main;
import com.quantum625.networks.Network;
import com.quantum625.networks.utils.Location;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class Config {

    private Main plugin;
    private File file;
    private Economy economy;
    public FileConfiguration config;
    private boolean economyState;

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Config(Main plugin, @Nonnull Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
        this.file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        if (config.get("lang") == null) {
            config.set("lang", "en");
            Bukkit.getLogger().warning("[Networks] Config for language is missing, it was reset to en");
        }

        if (config.get("tickrate") == null) {
            config.set("tickrate", 100);
            Bukkit.getLogger().warning("[Networks] Config for tickrate is missing, it was reset to 100");
        }

        if (config.get("container_whitelist") == null) {
            config.set("container_whitelist", Arrays.asList("CHEST", "REDSTONE_CHEST", "BARREL"));
            Bukkit.getLogger().warning("[Networks] Config for container_whitelist is missing, it was reset to [CHEST, REDSTONE_CHEST, BARREL]");
        }

        if (config.get("notice") == null) {
            config.set("notice", true);
            Bukkit.getLogger().warning("[Networks] Config for notice is missing, it was reset to true");
        }
    }

    public boolean noticeEnabled() {return Boolean.parseBoolean(config.get("notice").toString());}

    public void setLanguage(String language) {
        config.set("lang", language);
    }

    public String getLanguage() {
        return config.get("lang").toString();
    }

    public void setTickrate(int tickrate) {config.set("tickrate", tickrate);}
    public int getTickrate() {return Integer.parseInt(config.get("tickrate").toString());}

    public int getBaseContainers() {return Integer.parseInt(config.get("base_container_limit").toString());}
    public int getBaseRange() {return Integer.parseInt(config.get("base_range").toString());}
    public void setEconomyState(boolean state) {economyState = state;}


    public double calculateRefund(Network network) {
        double refund = get("refund_create");
        refund += (network.getMaxContainers() - get("base_container_limit")) * get("refund_container_limit");
        refund += (network.getMaxRange() - get("base_range")) * get("refund_range");
        return refund;
    }

    public int buyFeature(Player player, String feature, int existingUpgrade , int amount) {
        if (economyState) {
            if (economy != null) {

                if (config.get("cost_" + feature) == null) {
                    Bukkit.getLogger().warning("No price set for " + feature);
                    return BUY_RESULT_NOPRICE;
                }
                int price = getPrice(feature) * amount;

                if (existingUpgrade + amount > Integer.parseInt(config.get("max_" + feature).toString())) {
                    return BUY_RESULT_MAXED;
                }

                if (price < 0) {
                    return BUY_RESULT_DISABLED;
                }

                if (economy.getBalance(Bukkit.getServer().getOfflinePlayer(player.getUniqueId())) >= price) {
                    economy.withdrawPlayer(Bukkit.getServer().getOfflinePlayer(player.getUniqueId()), price);
                    Bukkit.getLogger().info("[Networks] Player " + player.getName() + " bought " + feature + " " + amount + " times");
                    return BUY_RESULT_SUCCESS;
                }

                else {
                    return BUY_RESULT_NO_MONEY;
                }
            }
            else {
                return BUY_RESULT_NOECO;
            }
        }
        return BUY_RESULT_DISABLED;
    }


    public int BUY_RESULT_NOPRICE = -4;
    public int BUY_RESULT_NOECO = -3;
    public int BUY_RESULT_MAXED = -2;
    public int BUY_RESULT_DISABLED = -1;
    public int BUY_RESULT_NO_MONEY = 0;
    public int BUY_RESULT_SUCCESS = 1;

    public int getPrice(String feature) {
        return Integer.parseInt(config.get("cost_"+ feature).toString());
    }
    public int get(String id) {
        return Integer.parseInt(config.get(id).toString());
    }

    public boolean checkLocation(Location location, String component) {
        return (config.get("container_whitelist").toString().toUpperCase().contains(location.getBukkitLocation().getBlock().getType().toString().toUpperCase()));
    }
}
