package com.quantum625.autosort.commands;

import com.quantum625.autosort.NetworkManager;
import com.quantum625.autosort.StorageNetwork;
import com.quantum625.autosort.container.BaseContainer;
import com.quantum625.autosort.container.InputContainer;
import com.quantum625.autosort.container.ItemContainer;
import com.quantum625.autosort.container.MiscContainer;
import com.quantum625.autosort.utils.Location;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class CommandListener implements CommandExecutor{

    private File dataFolder;
    private NetworkManager net;


    public CommandListener(File dataFolder) {
        this.dataFolder = dataFolder;
        this.net = new NetworkManager(dataFolder);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof BlockCommandSender) {
            Bukkit.getLogger().warning("Command Blocks are not allowed to use the autosort command!");
            return true;
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
                        returnMessage(sender,"Reloaded data from files");
                        loadData();
                    }
                    if (args[1].equalsIgnoreCase("save")) {
                        returnMessage(sender, "Saved data");
                        saveData();
                    }
                }
            }


            else if (args[0].equalsIgnoreCase("create")) {

                UUID owner;

                if (sender instanceof Player) {
                    owner = ((Player) sender).getUniqueId();
                }

                else {
                    if (args.length == 3) {
                        owner = UUID.fromString(args[2]);
                    }
                    else {
                        Bukkit.getLogger().warning("Command was not executed by player!\nPlease specify the owner\n/as create (name) (owner)");
                        return true;
                    }
                }

                if (args[1] != null) {
                    net.add(args[1], owner);
                    if (net.getFromID(args[1]) != null) {
                        returnMessage(sender, "Successfully created network " + args[1]);
                    }
                    else {
                        returnMessage(sender, "Failed creating a storage network");
                    }

                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("delete")) {
                if (args[1] != null) {
                    if (sender instanceof Player) {
                        if (!net.getFromID(args[1]).getOwner().equals((Player) sender) && !((Player) sender).hasPermission("autosort.admin")) {
                            returnMessage(sender, "You do not have permission to delete this network!");
                            returnMessage(sender, "You need to be the network owner or have the autosort.admin permission!");
                            return true;
                        }
                    }
                    net.delete(args[1]);
                    returnMessage(sender, "Successfully deleted network " + args[1]);
                    return true;
                }
                returnMessage(sender, "Please specify the network you want to delete!");
                return true;
            }

            else if (args[0].equalsIgnoreCase("select")) {

                if (args.length < 2) {
                    returnMessage(sender, "Please specify the network you want to select!");
                    return true;
                }

                if (sender instanceof Player) {
                    net.selectNetwork((Player) sender, net.getFromID(args[1]));
                }

                if (sender instanceof ConsoleCommandSender) {
                    net.consoleSelectNetwork(net.getFromID(args[1]));
                }

                returnMessage(sender, "Successfully selected network " + args[1]);

                return true;
            }

            else if (args[0].equalsIgnoreCase("info")) {
                returnMessage(sender, "This feature is not implemented yet!");
                return true;
            }

            else if (args[0].equalsIgnoreCase("list")) {

                UUID owner;

                if (sender instanceof Player) {
                    owner = ((Player) sender).getUniqueId();
                }

                else {
                    if (args.length == 3) {
                        owner = Bukkit.getPlayer(args[2]).getUniqueId();
                    }
                    else {
                        Bukkit.getLogger().warning("Command was not executed by player!\nPlease specify the player\n/as list (player)");
                        return true;
                    }
                }

                Bukkit.getLogger().info(net.listFromOwner(owner).toString());
                if (net.listFromOwner(owner).isEmpty()) {
                    returnMessage(sender, "You don't own any storage networks!");
                }
                else {
                    returnMessage(sender, "You own the following storage networks:");
                    for (int i = 0; i < net.listFromOwner(owner).size(); i++) {
                        returnMessage(sender, net.listFromOwner(owner).get(i).getID().toString());
                    }
                }

                return true;

            }

            else if (args[0].equalsIgnoreCase("listall")) {
                Bukkit.getLogger().info(net.listAll().toString());
                if (net.listAll().isEmpty()) {
                    returnMessage(sender, "There are no storage networks!");
                }
                else {
                    returnMessage(sender, "Theses storage networks exist:");
                    for (int i = 0; i < net.listAll().size(); i++) {
                        returnMessage(sender, net.listAll().get(i).getID().toString());
                    }
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("container")) {

                Location pos = new Location(0, 0, 0, "world");
                StorageNetwork network;

                if (args.length < 2) {
                    returnMessage(sender, "You must specify the container type!");
                    return true;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    pos = new Location(player.getTargetBlock(null, 5));
                    network = net.getSelectedNetwork(player);

                    if (args[1].equalsIgnoreCase("input")) {
                        network.addInputChest(pos);
                    }

                    else if (args[1].equalsIgnoreCase("sorting")) {
                        if (args.length < 3) {
                            returnMessage(sender, "You need to specify the item to sort");
                        }
                        network.addItemChest(pos, args[2].toUpperCase());
                    }

                    else if (args[1].equalsIgnoreCase("misc")) {
                        network.addMiscChest(pos, false);
                    }
                }

                else {
                    if (args.length < 5) {
                        returnMessage(sender, "You need to specify a container location");
                        returnMessage(sender, "/as container <type> <x> <y> <z> <world>");
                    }
                    pos.setX(Integer.parseInt(args[2]));
                    pos.setY(Integer.parseInt(args[3]));
                    pos.setZ(Integer.parseInt(args[4]));
                    pos.setDim(args[5]);

                    network = net.getConsoleSelection();

                    if (args[1].equalsIgnoreCase("input")) {
                        network.addInputChest(pos);
                        returnMessage(sender, "Added input container to your network");
                        return true;
                    }

                    else if (args[1].equalsIgnoreCase("sorting")) {
                        if (args.length < 6) {
                            returnMessage(sender, "You need to specify the item to sort");
                        }
                        network.addItemChest(pos, args[6].toUpperCase());
                        returnMessage(sender, "Added sorting container to your network");
                        return true;
                    }

                    else if (args[1].equalsIgnoreCase("misc")) {
                        network.addMiscChest(pos, false);
                        returnMessage(sender, "Added miscellaneous container to your network");
                        return true;

                    }
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("sort")) {
                if (sender instanceof Player) {
                    net.getSelectedNetwork((Player) sender).sortAll();
                }
                else {
                    net.getConsoleSelection().sortAll();
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("checkinv")) {
                if (args.length > 4) {
                    BaseContainer container = new BaseContainer(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]),args[4]);
                    returnMessage(sender, String.valueOf(container.getInventory().getSize()));
                }
                return true;
            }

            else {
                returnMessage(sender, "Invalid command, type /as help to see all available commands");
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

            for (int i = 0; i < playerHelpMessage.toArray().length; i++) {
                sendJSONMessage(player, playerHelpMessage.get(i).toString());
            }
        }

        if (sender instanceof BlockCommandSender) {
            System.out.println(helpMessage);
        }

        if (sender instanceof ConsoleCommandSender) {
            System.out.println(helpMessage);
        }

    }

    private void sendJSONMessage(Player player, String message) {
        player.performCommand("execute as " + player.getUniqueId() + " run tellraw @s " + message);

    }



    private List playerHelpMessage = Arrays.asList(
            "[\"\",{\"text\":\"       Autosort Plugin - Version 1.0.0 ========================================\",\"bold\":true,\"color\":\"dark_green\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/as help <command>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as help\"}},{\"text\":\" - \"},{\"text\":\"Help for a command\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/as help <page>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as help\"}},{\"text\":\" - \"},{\"text\":\"Show this menu\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/as create <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as create\"}},{\"text\":\" - \"},{\"text\":\"Create a storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/as delete <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as delete\"}},{\"text\":\" - \"},{\"text\":\"Delete a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/as select <network>\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as select\"}},{\"text\":\" - \"},{\"text\":\"Select a storage network\",\"color\":\"yellow\"}]",
            "\"\"",
            "[\"\",{\"text\":\"/as info\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as info\"}},{\"text\":\" - \"},{\"text\":\"Show the stats of your storage network\",\"color\":\"yellow\"}]",
            "[\"\",{\"text\":\"/as list\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/as list\"}},{\"text\":\" - \"},{\"text\":\"List all your storage networks\",\"color\":\"yellow\"}]"
    );

    private String helpMessage = """

       Autosort Plugin - Version 1.0.0 
==========================================

/as help <command> - Help for a command
/as help <page> - Show this menu

/as create <network> - Create a storage network
/as delete <network> - Delete a storage network

/as select <network> - Select a storage network

/as info - Show the stats of your storage network
/as list - List all your storage networks

""";

    public void saveData() {
        net.saveData();
    }

    public void loadData() {
        net.loadData();
    }

}
