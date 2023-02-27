package net.quantum625.networks.commands;

import net.quantum625.networks.Installer;
import net.quantum625.networks.Network;
import net.quantum625.networks.data.Language;
import net.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;


public class LanguageModule {

    private Language language;

    public LanguageModule(JavaPlugin plugin, Installer installer, String lang_id) {
        this.language = new Language(plugin, installer, lang_id);
    }

    public String getItemName(String key) {
        return language.getText("item.name."+ key);
    }
    public String getItemName(String key, int level) {
        return language.getText("item.name."+ key).replace("%level", ""+level);
    }


    public List<String> getItemLore(String key) {
        return Arrays.stream(language.getText("item.lore."+ key).replace("[", "").replace("]","").split(", ")).toList();
    }

    public void message(CommandSender sender, String message) {
        if (sender instanceof Player) {
            ((Player) sender).sendMessage(message);
        }

        else {
            Bukkit.getLogger().info("§9[Networks] §f" + message);
        }
    }

    public void returnMessage(CommandSender sender, String id) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id));
        }
    }

    public void returnMessage(CommandSender sender, String id, String text) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%text", text));
        }
    }

    public void returnMessage(CommandSender sender, String id, Player player) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%player", player.getDisplayName()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network1, Network network2) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%net1", network1.getID()).replaceAll("%net2", network2.getID()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network, double value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%value", String.valueOf(value)));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network, int value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%value", String.valueOf(value)));
        }
    }

    public void returnMessage(CommandSender sender, String id, Location location) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%position", location.toString()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network, Location location) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()));
        }
    }


    public void returnMessage(CommandSender sender, String id, Network network, Location location, int value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()).replaceAll("%value", ""+value));
        }
    }


    public void returnMessage(CommandSender sender, String id, Network network, Location location, String[] items) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
            return;
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            String list = "";
            for (String item : items) {
                list += "\n" + item.toUpperCase();
            }
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()).replaceAll("%items", list));
        }
    }
    public void returnMessage(CommandSender sender, String id, Network network, Location location, int value, String[] items) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
            return;
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            String list = "";
            for (String item : items) {
                list += "\n" + item.toUpperCase();
            }
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()).replaceAll("%items", list).replaceAll("%value", ""+value));
        }
    }
    public void returnMessage(CommandSender sender, String id, Material material) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%material", material.toString().toUpperCase()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Location location,  Material material) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%material", material.toString().toUpperCase()).replaceAll("%position", location.toString()));
        }
    }


    public void returnMessage(CommandSender sender, String id, double value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
            message(sender, "Try restarting the server (NOT reload!)");
        }

        else {
            message(sender, language.getText(id).replaceAll("%value", String.valueOf(value)));
        }
    }


}
