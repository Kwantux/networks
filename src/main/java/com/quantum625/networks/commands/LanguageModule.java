package com.quantum625.networks.commands;

import com.quantum625.networks.Network;
import com.quantum625.networks.data.Language;
import com.quantum625.networks.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;


public class LanguageModule {

    private Language language;

    public LanguageModule(File datafolder, String lang_id) {
        this.language = new Language(datafolder, lang_id);
    }

    private void message(CommandSender sender, String message) {
        if (sender instanceof Player) {
            ((Player) sender).sendMessage(message);
        }

        else {
            Bukkit.getLogger().info("[Networks] " + message);
        }
    }

    public void returnMessage(CommandSender sender, String id) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
        }

        else {
            message(sender, language.getText(id));
        }
    }

    public void returnMessage(CommandSender sender, String id, Player player) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
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
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Network network, double value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
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
        }

        else {
            message(sender, language.getText(id).replaceAll("%network", network.getID()).replaceAll("%position", location.toString()));
        }
    }

    public void returnMessage(CommandSender sender, String id, Block block) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
        }

        else {
            message(sender, language.getText(id).replaceAll("%block", block.getType().toString()));
        }
    }

    public void returnMessage(CommandSender sender, String id, double value) {
        if (language == null) {
            message(sender, "ERROR: Language module not found, please contact your system administrator");
        }

        if (language.getText(id) == null) {
            message(sender, "ERROR: No language key found for " + id);
        }

        else {
            message(sender, language.getText(id).replaceAll("%number", String.valueOf(value)));
        }
    }


}
