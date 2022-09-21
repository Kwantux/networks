package com.quantum625.networks.commands;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.Network;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements TabExecutor {

    private NetworkManager net;

    public TabCompleter(NetworkManager net) {
        this.net = net;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {return true;}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player) {
                if (!((Player) sender).hasPermission("networks.admin")) {
                    return commands;
                }
            }
            return adminCommands;
        }

        else if (args[0].equalsIgnoreCase("component")) {
            if (args.length == 2) {
                return Arrays.asList("add");
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("add")) {
                    return Arrays.asList("input", "sorting", "misc");
                }
            }

        }

        else if (args[0].equalsIgnoreCase("data")) {
            if (args.length == 2) {
                return Arrays.asList("reload", "save");
            }

        }

        else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("select")) {
            if (args.length == 2) {
                List<String> list = new ArrayList<String>();
                if (sender instanceof Player) {
                    if (!((Player) sender).hasPermission("networks.admin")) {
                        for (Network network : net.listFromOwner(((Player) sender).getUniqueId())) {
                            list.add(network.getID());
                        }
                        return list;
                    }
                }
                for (Network network : net.listAll()) {
                    list.add(network.getID());
                }
                return list;
            }
        }

        Bukkit.getLogger().info(String.valueOf(args.length));
        Bukkit.getLogger().info(args[1]);
        return Arrays.asList();
    }

    private List<String> adminCommands = Arrays.asList("component", "create", "data", "delete", "help" ,"info", "list", "listall", "select", "sort");
    private List<String> commands = Arrays.asList("component", "create", "delete", "help" ,"info", "list", "listall", "select");
}