package com.quantum625.networks.commands;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.Network;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.SortingContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.utils.Location;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class CommandListener implements CommandExecutor {

    private File dataFolder;
    private NetworkManager net;

    private LanguageModule lang;

    private Config config;

    private Economy economy;

    private Player consolePlayer;


    public CommandListener(NetworkManager net, File dataFolder, LanguageModule lang, Config config, Economy economy) {
        this.dataFolder = dataFolder;
        this.net = net;
        this.lang = lang;
        this.config = config;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player;
        UUID uid;

        if (sender instanceof Player) {
            player = (Player) sender;
            uid = player.getUniqueId();
        }

        else {
            Bukkit.getLogger().warning("[Networks] Console commands are currently not supported");
            return true;
            /*if (args.length > 1) {
                if (args[0].equalsIgnoreCase("selectplayer")) {
                    consolePlayer = Bukkit.getPlayer(args[1]);
                }
            }

            if (consolePlayer != null) {
                player = consolePlayer;
                uid = player.getUniqueId();
            }

            else {
                lang.returnMessage(sender, "player.noselection");
                return true;
            }*/
        }

        if (args.length == 0) {
            sendHelp(sender);
        }

        else {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender);
                return true;
            }

            else if (args[0].equalsIgnoreCase("data")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("reload")) {
                        loadData();
                        lang.returnMessage(sender, "data.load");
                    }
                    if (args[1].equalsIgnoreCase("save")) {
                        saveData();
                        lang.returnMessage(sender, "data.save");
                    }
                }
            }

            /*else if (args[0].equalsIgnoreCase("statistics")) {
                if (sender instanceof Player && !((Player) sender).hasPermission("networks.admin")) {
                    lang.returnMessage(sender, "permission.server");
                }
                lang.returnMessage(sender, "statistics");
                for (Network network : net.listAll()) {
                    returnMessage(sender, "&l" + network.getID() + ":&r " +  ,);
                }
            }*/

            else if (args[0].equalsIgnoreCase("create")) {

                if (args[1] != null) {
                    if (net.getFromID(args[1]) != null) {
                        lang.returnMessage(sender, "create.exists");
                    } else {
                        if (config.getEconomyState()) {
                            if (sender instanceof Player) {
                                if (config.getPrice("create") >= economy.getBalance((Player) sender)) {
                                    lang.returnMessage(sender, "create.nomoney", config.getPrice("create") - economy.getBalance((Player) sender));
                                    return true;
                                }
                                economy.withdrawPlayer(Bukkit.getOfflinePlayer(((Player) sender).getUniqueId()), config.getPrice("create"));
                            }

                            net.add(args[1], uid);
                            if (net.getFromID(args[1]) != null) {
                                lang.returnMessage(sender, "create.success.eco", net.getFromID(args[1]), config.getPrice("create"));
                                net.selectNetwork((Player) sender, net.getFromID(args[1]));
                            } else {
                                lang.returnMessage(sender, "create.fail");
                            }
                        }
                        else {
                            net.add(args[1], uid);
                            lang.returnMessage(sender, "create.success", net.getFromID(args[1]));
                        }
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args[1] != null) {
                    if (sender instanceof Player) {
                        if (!net.getFromID(args[1]).getOwner().equals(((Player) sender).getUniqueId()) && !((Player) sender).hasPermission("networks.admin")) {
                            lang.returnMessage(sender, "nopermission");
                            return true;
                        }
                    }
                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("confirm")) {
                            if (config.getEconomyState()) {
                                lang.returnMessage(sender, "delete.success.eco", net.getFromID(args[1]), config.calculateRefund(net.getFromID(args[1])));
                                economy.depositPlayer(Bukkit.getOfflinePlayer(net.getFromID(args[1]).getOwner()), config.calculateRefund(net.getFromID(args[1])));
                                net.delete(args[1]);
                                return true;
                            }
                            else {
                                lang.returnMessage(sender, "delete.success", net.getFromID(args[1]));
                                net.delete(args[1]);
                                return true;
                            }
                        }
                    }
                    lang.returnMessage(sender, "delete.confirm", net.getFromID(args[1]));
                    return true;
                }
                lang.returnMessage(sender, "delete.nonetwork");
                return true;
            } else if (args[0].equalsIgnoreCase("select")) {

                if (args.length < 2) {
                    returnMessage(sender, "select.nonetwork");
                    return true;
                }

                if (sender instanceof Player) {
                    if (net.checkNetworkPermission((Player) sender, net.getFromID(args[1])) > 0) {
                        net.selectNetwork((Player) sender, net.getFromID(args[1]));
                    } else {
                        lang.returnMessage(sender, "permission.user");
                        return true;
                    }
                }

                if (sender instanceof ConsoleCommandSender) {
                    net.consoleSelectNetwork(net.getFromID(args[1]));
                }

                lang.returnMessage(sender, "select.success", net.getFromID(args[1]));

                return true;
            } else if (args[0].equalsIgnoreCase("info")) {
                Network network = getSelected(sender);
                if (network == null) {
                    lang.returnMessage(sender, "select.noselection");
                    return true;
                }

                returnMessage(sender, "");
                returnMessage(sender, "                §lNetwork Information");
                returnMessage(sender, "=============================================");
                returnMessage(sender, "");
                returnMessage(sender, "Name: " + network.getID());
                returnMessage(sender, "Owner: " + Bukkit.getOfflinePlayer(network.getOwner()).getName());


                String users = "Users: ";
                for (UUID uuid : network.getUsers()) {
                    users += Bukkit.getOfflinePlayer(uid).getName() + ", ";
                }
                returnMessage(sender, users);


                returnMessage(sender, "");
                if (config.getEconomyState()) returnMessage(sender, "Containers: " + network.getAllComponents().size() + "/" + network.getMaxContainers());
                else returnMessage(sender, "Containers: " + network.getAllComponents().size());
                returnMessage(sender, "Max Range: " + network.getMaxRange());
                returnMessage(sender, "");


                returnMessage(sender, "Input Containers: ");
                for (InputContainer inputContainer : network.getInputChests()) {
                    returnMessage(sender, "X: " + inputContainer.getPos().getX() + " Y: " + inputContainer.getPos().getY() + " Z: " + inputContainer.getPos().getZ() + " World: " + inputContainer.getPos().getDim());
                }
                returnMessage(sender, "");


                returnMessage(sender, "Item Containers: ");
                for (SortingContainer sortingContainer : network.getSortingChests()) {
                    returnMessage(sender, "X: " + sortingContainer.getPos().getX() + " Y: " + sortingContainer.getPos().getY() + " Z: " + sortingContainer.getPos().getZ() + " World: " + sortingContainer.getPos().getDim());
                }
                returnMessage(sender, "");


                returnMessage(sender, "Miscellaneous Containers: ");
                for (MiscContainer miscContainer : network.getMiscChests()) {
                    returnMessage(sender, "X: " + miscContainer.getPos().getX() + " Y: " + miscContainer.getPos().getY() + " Z: " + miscContainer.getPos().getZ() + " World: " + miscContainer.getPos().getDim());
                }
                returnMessage(sender, "");


                return true;
            } else if (args[0].equalsIgnoreCase("list")) {

                if (net.listFromUser(uid).isEmpty()) {
                    lang.returnMessage(sender, "list.empty");
                } else {
                    lang.returnMessage(sender, "list");
                    for (int i = 0; i < net.listFromUser(uid).size(); i++) {
                        returnMessage(sender, net.listFromUser(uid).get(i).getID().toString());
                    }
                }

                return true;

            } else if (args[0].equalsIgnoreCase("listall")) {
                if (sender instanceof Player && !((Player) sender).hasPermission("networks.admin")) {
                    lang.returnMessage(sender, "permission.server");
                    return true;
                }

                if (net.listAll().isEmpty()) {
                    lang.returnMessage(sender, "listall.empty");
                } else {
                    lang.returnMessage(sender, "listall");
                    for (int i = 0; i < net.listAll().size(); i++) {
                        returnMessage(sender, net.listAll().get(i).getID().toString());
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("component")) {

                if (!config.getEconomyState()) {
                    return true;
                }

                Location pos = new Location(0, 0, 0, "world");
                Network network;

                if (args.length < 2) {
                    lang.returnMessage(sender, "component.notype");
                    return true;
                }

                network = net.getSelectedNetwork(player);

                if (net.checkNetworkPermission(player, network) < 1) {
                    lang.returnMessage(sender, "permission.user");
                    return true;
                }

                if (network == null) {
                    lang.returnMessage(sender, "select.noselection");
                    return true;
                }

                if (args[1].equalsIgnoreCase("add")) {

                    if (network.checkContainerLimit()) {

                        if (args.length < 3) {
                            lang.returnMessage(sender, "component.noaction");
                            return true;
                        }

                        if (args[2].equalsIgnoreCase("input")) {
                            net.selectComponentType(player, "input_container");
                            lang.returnMessage(sender, "component.select");
                        } else if (args[2].equalsIgnoreCase("sorting")) {
                            if (args.length < 4) {
                                lang.returnMessage(sender, "component.item.noitem");
                            }
                            net.selectComponentType(player, "item_container");

                            String[] items = new String[args.length-2];

                            for (int i = 2; i < args.length; i++) {
                                items[i-2] = args[i].toUpperCase();
                            }

                            net.selectItems(player, (String[]) items);
                            lang.returnMessage(sender, "component.select");
                        } else if (args[2].equalsIgnoreCase("misc")) {
                            net.selectComponentType(player, "misc_container");
                            lang.returnMessage(sender, "component.select");
                        }
                    } else {
                        lang.returnMessage(sender, "component.limit");
                    }

                } else if (args[1].equalsIgnoreCase("edit")) {

                } else if (args[1].equalsIgnoreCase("remove")) {
                    network.removeComponent(pos);
                } else {
                    returnMessage(sender, "component.noaction");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("upgrade")) {

                if (!config.getEconomyState()) return true;

                int amount = 1;

                if (args.length > 2) {
                    amount = Integer.parseInt(args[2]);

                    if (amount < 1) {
                        amount = 1;
                        lang.returnMessage(sender, "value.notpositive");
                    }
                }

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("limit")) {

                        if (net.getSelectedNetwork(player) == null) {
                            lang.returnMessage(sender, "select.noselection");
                            return true;
                        }

                        int buyResult = config.buyFeature(player, "container_limit", net.getSelectedNetwork(player).getMaxContainers(), amount);

                        if (buyResult == config.BUY_RESULT_NOPRICE) {
                            lang.returnMessage(sender, "upgrade.noprice");
                        }

                        if (buyResult == config.BUY_RESULT_NOECO) {
                            lang.returnMessage(sender, "upgrade.noeconomy");
                        }

                        if (buyResult == config.BUY_RESULT_MAXED) {
                            lang.returnMessage(sender, "upgrade.maxed");
                        }

                        if (buyResult == config.BUY_RESULT_DISABLED) {
                            lang.returnMessage(sender, "upgrade.disabled");
                        }

                        if (buyResult == config.BUY_RESULT_NO_MONEY) {
                            lang.returnMessage(sender, "upgrade.nomoney", config.getPrice("container_limit") * amount - economy.getBalance(player));
                        }

                        if (buyResult == config.BUY_RESULT_SUCCESS) {
                            lang.returnMessage(sender, "upgrade.success", config.getPrice("container_limit") * amount);
                            net.getSelectedNetwork(player).upgradeLimit(amount);
                        }
                    }

                    if (args[1].equalsIgnoreCase("range")) {
                        if (net.getSelectedNetwork(player) == null) {
                            lang.returnMessage(sender, "select.noselection");
                            return true;
                        }

                        int buyResult = config.buyFeature(player, "range", net.getSelectedNetwork(player).getMaxRange(), amount);

                        if (buyResult == config.BUY_RESULT_NOPRICE) {
                            lang.returnMessage(sender, "upgrade.noprice");
                        }

                        if (buyResult == config.BUY_RESULT_NOECO) {
                            lang.returnMessage(sender, "upgrade.noeconomy");
                        }

                        if (buyResult == config.BUY_RESULT_MAXED) {
                            lang.returnMessage(sender, "upgrade.maxed");
                        }

                        if (buyResult == config.BUY_RESULT_DISABLED) {
                            lang.returnMessage(sender, "upgrade.disabled");
                        }

                        if (buyResult == config.BUY_RESULT_NO_MONEY) {
                            lang.returnMessage(sender, "upgrade.nomoney", config.getPrice("range") * amount - economy.getBalance(player));
                        }

                        if (buyResult == config.BUY_RESULT_SUCCESS) {
                            lang.returnMessage(sender, "upgrade.success", config.getPrice("range") * amount);
                            net.getSelectedNetwork(player).upgradeRange(amount);
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("sort")) {
                getSelected(sender).sortAll();
                return true;
            } else if (args[0].equalsIgnoreCase("user")) {

                if (sender instanceof Player && net.checkNetworkPermission((Player) sender, net.getSelectedNetwork((Player) sender)) < 2) {
                    lang.returnMessage(sender, "permission.admin");
                    return true;
                }

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("add")) {
                        if (args.length > 2) {
                            getSelected(sender).addUser(Bukkit.getPlayer(args[2]).getUniqueId());
                            lang.returnMessage(sender, "user.add", player);
                            return true;
                        }
                        lang.returnMessage(sender, "user.specify");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("remove")) {
                        if (args.length > 2) {
                            getSelected(sender).removeUser(Bukkit.getPlayer(args[2]).getUniqueId());
                            lang.returnMessage(sender, "user.remove", player);
                            return true;
                        }
                        lang.returnMessage(sender, "user.specify");
                        return true;
                    }
                }

                lang.returnMessage(sender, "user.action");
                return true;
            } else if (args[0].equalsIgnoreCase("owner")) {
                if (getSelected(sender) == null) {
                    lang.returnMessage(sender, "select.noselection");
                }
                if (args.length > 1) {
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (targetPlayer != null) {
                        getSelected(sender).setOwner(targetPlayer.getUniqueId());
                        return true;
                    }
                    lang.returnMessage(sender, "user.notexisting");
                    return true;
                }
                lang.returnMessage(sender, "user.specify");
                return true;
            }
        }

        return true;
    }


    private void returnMessage(CommandSender sender, String text) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
            player.sendMessage(text);

        }

        if (sender instanceof ConsoleCommandSender) {
            Bukkit.getLogger().info(text);
        }
    }

    private void sendHelp(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("networks.admin")) {
                for (int i = 0; i < adminHelpMessage.toArray().length; i++) {
                    sendJSONMessage(player, adminHelpMessage.get(i).toString());
                }
                return;
            }

            for (int i = 0; i < playerHelpMessage.toArray().length; i++) {
                sendJSONMessage(player, playerHelpMessage.get(i).toString());
            }
            return;
        }

        Bukkit.getLogger().info(helpMessage);

    }

    private void sendJSONMessage(Player player, String message) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "execute as " + player.getUniqueId() + " run tellraw @s " + message);

    }


    private Network getSelected(CommandSender sender) {
        Network result;
        if (sender instanceof Player) {
            result =  net.getSelectedNetwork((Player) sender);
        }
        else {
            result =  net.getConsoleSelection();
        }
        return result;
    }

    private List playerHelpMessage = Arrays.asList(
            "\"\"",
            "[\"\",{\"text\":\"       Networks Plugin - Version 1.0.0 ========================================\",\"bold\":true,\"color\":\"dark_green\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net help\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net help\"}},{\"text\":\" - \"},{\"text\":\"Show this menu\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net create <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net create\"}},{\"text\":\" - \"},{\"text\":\"Create a storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net delete <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net delete\"}},{\"text\":\" - \"},{\"text\":\"Delete a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net select <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net select\"}},{\"text\":\" - \"},{\"text\":\"Select a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net info\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net info\"}},{\"text\":\" - \"},{\"text\":\"Show the stats of your storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net list\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net list\"}},{\"text\":\" - \"},{\"text\":\"List all your storage networks\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net component add <type>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net component add\"}},{\"text\":\" - \"},{\"text\":\"Add components to your network\",\"color\":\"yellow\"}]",
            "\"\""
    );

    private List adminHelpMessage = Arrays.asList(
            "\"\"",
            "[\"\",{\"text\":\"       Networks Plugin - Version 1.0.0 ========================================\",\"bold\":true,\"color\":\"dark_green\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net help\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net help\"}},{\"text\":\" - \"},{\"text\":\"Show this menu\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net data reload\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net data reload\"}},{\"text\":\" - \"},{\"text\":\"Reload network files\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net data save\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net data save\"}},{\"text\":\" - \"},{\"text\":\"Save network files\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net create <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net create\"}},{\"text\":\" - \"},{\"text\":\"Create a storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net delete <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net delete\"}},{\"text\":\" - \"},{\"text\":\"Delete a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net select <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net select\"}},{\"text\":\" - \"},{\"text\":\"Select a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net info\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net info\"}},{\"text\":\" - \"},{\"text\":\"Show the stats of your storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net list\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net list\"}},{\"text\":\" - \"},{\"text\":\"List all your storage networks\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/net listall\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net listall\"}},{\"text\":\" - \"},{\"text\":\"List all storage networks\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/net component add <type>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/net component add\"}},{\"text\":\" - \"},{\"text\":\"Add components to your network\",\"color\":\"yellow\"}]",
            "\"\""
    );

    private String helpMessage = """

       Networks Plugin - Version 1.0.0 
==========================================

/net help - Show this menu

/net data reload - Reload network files
/net data save - Save network files

/net create <network> - Create a storage network
/net delete <network> - Delete a storage network

/net select <network> - Select a storage network

/net info - Show the stats of your storage network
/net list - List all your storage networks
/net listall - List all storage networks

/net component add - Add a new component to the storage network

""";

    public void saveData() {
        net.saveData();
    }

    public void loadData() {
        net.loadData();
    }

}
