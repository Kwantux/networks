package net.quantum625.networks.commands;

import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.Network;
import net.quantum625.networks.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class TabCompleter implements TabExecutor {

    private NetworkManager net;
    private Config config;

    public TabCompleter(NetworkManager net, Config config) {
        this.net = net;
        this.config = config;
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
                    if (config.getEconomyState()) {
                        return userSelectionEconomy;
                    }
                    return userSelection;
                }
                if (net.getSelectedNetwork((Player)sender) == null) {
                    return adminNoSelection;
                }
                if (config.getEconomyState()) {
                    return adminSelectionEconomy;
                }
                return adminSelection;
            }
            if (net.getConsoleSelection() == null) {
                return adminNoSelection;
            }
            if (config.getEconomyState()) {
                return adminSelectionEconomy;
            }
            return adminSelection;
        }

        else if (args[0].equalsIgnoreCase("component")) {
            if (args.length == 2) {
                return Arrays.asList("add");
            }

            if (args.length >= 3) {
                if (args[1].equalsIgnoreCase("add")) {

                    if (args.length > 3) {
                        if (args[2].equalsIgnoreCase("sorting")) {
                            List<String> list = new ArrayList<String>();
                            for (Material m : Material.values()) {
                                if (m.name().toLowerCase().contains(args[args.length-1].toLowerCase()) || args[args.length-1].equalsIgnoreCase("")) {
                                    list.add(m.name().toLowerCase());
                                }
                            }
                            list.sort(Comparator.naturalOrder());
                            return list;
                        }
                    }
                    if (args.length == 3) {
                        return Arrays.asList("input", "sorting", "misc");
                    }
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

        else if (args[0].equalsIgnoreCase("merge")) {
            if (args.length == 2 || args.length == 3) {
                List<String> list = new ArrayList<String>();
                if (sender instanceof Player) {
                    if (!((Player) sender).hasPermission("networks.admin")) {
                        for (Network network : net.listFromOwner(((Player) sender).getUniqueId())) {
                            if (args.length != 3 || !args[1].replace(" ", "").equalsIgnoreCase(network.getID())) {
                                list.add(network.getID());
                            }
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

    private List<String> adminSelection = Arrays.asList("create", "data", "delete", "help" ,"info", "items", "list", "listall", "merge", "owner", "select", "sort", "user", "view");
    private List<String> adminSelectionEconomy = Arrays.asList("component", "create", "data", "delete", "help" ,"info", "items", "list", "listall", "merge", "owner", "select", "sort", "upgrade", "user", "view");

    private List<String> adminNoSelection = Arrays.asList("create", "data", "delete", "help", "list", "listall", "merge", "select");


    private List<String> userSelection = Arrays.asList("create", "delete", "help" ,"info", "items", "list", "merge", "owner", "select", "user", "view");
    private List<String> userSelectionEconomy = Arrays.asList("component", "create", "delete", "help" ,"info", "items", "list", "merge", "owner", "select", "upgrade", "user", "view");

    private List<String> userNoSelection = Arrays.asList("create", "delete", "help" ,"list", "merge", "select");

}