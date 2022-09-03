package com.quantum625.autosort.commands;

import com.quantum625.autosort.StorageNetwork;
import com.quantum625.autosort.data.Language;
import com.quantum625.autosort.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;


public class LanguageModule {

    private Language language;

    public LanguageModule(File datafolder, String lang_id) {
        this.language = new Language(datafolder, lang_id);
    }

    public void returnMessage(CommandSender sender, String id) {
        if (sender instanceof Player) {
            if (language != null) {
                ((Player) sender).sendMessage(language.getPlayerText(id));
            }
            else {
                ((Player) sender).sendMessage("ERROR: Language module not found, please contact your system administrator");
            }
        }

        else {
            if (language != null) {
                Bukkit.getLogger().info("[Autosort]" + language.getConsoleText(id));
            }
            else {
                Bukkit.getLogger().warning("[Autosort] Language module not found!");
            }
        }
    }

    public void returnMessage(CommandSender sender, String id, StorageNetwork network) {
        if (sender instanceof Player) {
            if (language != null) {
                ((Player) sender).sendMessage(language.getPlayerText(id).replaceAll("%network", network.getID()));
            }
            else {
                ((Player) sender).sendMessage("ERROR: Language module not found, please contact your system administrator");
            }
        }

        else {
            if (language != null) {
                Bukkit.getLogger().info("[Autosort]" + language.getConsoleText(id).replaceAll("%network", network.getID()));
            }
            else {
                Bukkit.getLogger().warning("[Autosort] Language module not found!");
            }
        }
    }

    public void returnMessage(CommandSender sender, String id, Location location) {
        if (sender instanceof Player) {
            if (language != null) {
                ((Player) sender).sendMessage(language.getPlayerText(id).replaceAll("%position", location.toString()));
            }
            else {
                ((Player) sender).sendMessage("ERROR: Language module not found, please contact your system administrator");
            }
        }

        else {
            if (language != null) {
                Bukkit.getLogger().info("[Autosort]" + language.getConsoleText(id).replaceAll("%position", location.toString()));
            }
            else {
                Bukkit.getLogger().warning("[Autosort] Language module not found!");
            }
        }
    }

    public void returnMessage(CommandSender sender, String id, StorageNetwork network, Location location) {
        if (sender instanceof Player) {
            if (language != null) {
                ((Player) sender).sendMessage(language.getPlayerText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()));
            }
            else {
                ((Player) sender).sendMessage("ERROR: Language module not found, please contact your system administrator");
            }
        }

        else {
            if (language != null) {
                Bukkit.getLogger().info("[Autosort]" + language.getConsoleText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()));
            }
            else {
                Bukkit.getLogger().warning("[Autosort] Language module not found!");
            }
        }
    }


}
