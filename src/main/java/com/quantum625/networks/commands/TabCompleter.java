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
import java.util.UUID;

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
                    if (net.getSelectedNetwork((Player)sender) == null) {
                        return userNoSelection;
                    }
                    return userSelection;
                }
                if (net.getSelectedNetwork((Player)sender) == null) {
                    return adminNoSelection;
                }
                return adminSelection;
            }
            if (net.getConsoleSelection() == null) {
                return adminNoSelection;
            }
            return adminSelection;
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

        else if (args[0].equalsIgnoreCase("delete")) {
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

        else if (args[0].equalsIgnoreCase("select")) {
            if (args.length == 2) {
                List<String> list = new ArrayList<String>();
                if (sender instanceof Player) {
                    if (!((Player) sender).hasPermission("networks.admin")) {
                        for (Network network : net.listFromUser(((Player) sender).getUniqueId())) {
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

        else if (args[0].equalsIgnoreCase("upgrade")) {
            if (args.length == 2) {
                return Arrays.asList("limit", "range");
            }
        }

        else if (args[0].equalsIgnoreCase("owner")) {
            List<String> list = new ArrayList<String>();

            if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
                return list;
            }
        }


        else if (args[0].equalsIgnoreCase("user")) {
            if (args.length == 2) {
                return Arrays.asList("add", "remove");
            }
            if (args.length == 3) {

                List<String> list = new ArrayList<String>();

                if (args[1].equalsIgnoreCase("add")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    return list;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    for (UUID uid : net.getSelectedNetwork((Player)sender).getUsers()) {
                        list.add(Bukkit.getOfflinePlayer(uid).getName());
                    }
                }
            }
        }

        return Arrays.asList();
    }

    private List<String> adminSelection = Arrays.asList("component", "create", "data", "delete", "help" ,"info", "list", "listall", "owner", "select", "sort", "upgrade", "user");
    private List<String> adminNoSelection = Arrays.asList("create", "data", "delete", "help", "list", "listall", "select");
    private List<String> userSelection = Arrays.asList("component", "create", "delete", "help" ,"info", "list", "owner", "select", "upgrade", "user");
    private List<String> userNoSelection = Arrays.asList("create", "delete", "help" ,"list", "select");

}