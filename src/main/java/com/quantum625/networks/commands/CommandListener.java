package com.quantum625.networks.commands;

import com.quantum625.networks.NetworkManager;
import com.quantum625.networks.Network;
import com.quantum625.networks.component.InputContainer;
import com.quantum625.networks.component.ItemContainer;
import com.quantum625.networks.component.MiscContainer;
import com.quantum625.networks.data.Config;
import com.quantum625.networks.utils.Location;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
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


    public CommandListener(NetworkManager net, File dataFolder, LanguageModule lang, Config config) {
        this.dataFolder = dataFolder;
        this.net = net;
        this.lang = lang;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof BlockCommandSender) {
            Bukkit.getLogger().warning("Command Blocks are not allowed to use the /network command!");
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
                        loadData();
                        lang.returnMessage(sender,"data.load");
                    }
                    if (args[1].equalsIgnoreCase("save")) {
                        saveData();
                        lang.returnMessage(sender, "data.save");
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
                        lang.returnMessage(sender, "create.noowner");
                        return true;
                    }
                }

                if (args[1] != null) {
                    if (net.getFromID(args[1]) != null) {
                        lang.returnMessage(sender, "create.exists");
                    }
                    else {
                        net.add(args[1], owner);
                        if (net.getFromID(args[1]) != null) {
                            lang.returnMessage(sender, "create.success", net.getFromID(args[1]));
                        } else {
                            lang.returnMessage(sender, "create.fail");
                        }
                    }
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("delete")) {
                if (args[1] != null) {
                    if (sender instanceof Player) {
                        if (!net.getFromID(args[1]).getOwner().equals((Player) sender) && !((Player) sender).hasPermission("networks.admin")) {
                            lang.returnMessage(sender, "nopermission");
                            return true;
                        }
                    }
                    net.delete(args[1]);
                    lang.returnMessage(sender, "delete.success", net.getFromID(args[1]));
                    return true;
                }
                lang.returnMessage(sender, "delete.nonetwork");
                return true;
            }

            else if (args[0].equalsIgnoreCase("select")) {

                if (args.length < 2) {
                    returnMessage(sender, "select.nonetwork");
                    return true;
                }

                if (sender instanceof Player) {
                    net.selectNetwork((Player) sender, net.getFromID(args[1]));
                }

                if (sender instanceof ConsoleCommandSender) {
                    net.consoleSelectNetwork(net.getFromID(args[1]));
                }

                lang.returnMessage(sender, "select.success", net.getFromID(args[1]));

                return true;
            }

            else if (args[0].equalsIgnoreCase("info")) {
                Network network = getSelected(sender);
                if (network == null) {
                    lang.returnMessage(sender, "select.noselection");
                    return true;
                }

                returnMessage(sender, "Name: " + network.getID());
                returnMessage(sender, "Owner: " + network.getOwner());
                returnMessage(sender, "Input Containers: ");
                for (InputContainer inputContainer : network.getInputChests()) {
                    returnMessage(sender, "X: " + inputContainer.getPos().getX() + " Y: " + inputContainer.getPos().getY() + " Z: " + inputContainer.getPos().getZ() + " World: " + inputContainer.getPos().getDim());
                }
                returnMessage(sender, "Item Containers: ");
                for (ItemContainer itemContainer : network.getSortingChests()) {
                    returnMessage(sender, "X: " + itemContainer.getPos().getX() + " Y: " + itemContainer.getPos().getY() + " Z: " + itemContainer.getPos().getZ() + " World: " + itemContainer.getPos().getDim());
                }
                returnMessage(sender, "Miscellaneous Containers: ");
                for (MiscContainer miscContainer : network.getMiscChests()) {
                    returnMessage(sender, "X: " + miscContainer.getPos().getX() + " Y: " + miscContainer.getPos().getY() + " Z: " + miscContainer.getPos().getZ() + " World: " + miscContainer.getPos().getDim());
                }
                returnMessage(sender, "");

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
                        lang.returnMessage(sender, "list.noplayer");
                        return true;
                    }
                }

                Bukkit.getLogger().info(net.listFromOwner(owner).toString());
                if (net.listFromOwner(owner).isEmpty()) {
                    lang.returnMessage(sender, "list.empty");
                }
                else {
                    lang.returnMessage(sender, "list");
                    for (int i = 0; i < net.listFromOwner(owner).size(); i++) {
                        returnMessage(sender, net.listFromOwner(owner).get(i).getID().toString());
                    }
                }

                return true;

            }

            else if (args[0].equalsIgnoreCase("listall")) {
                Bukkit.getLogger().info(net.listAll().toString());
                if (net.listAll().isEmpty()) {
                    lang.returnMessage(sender, "listall.empty");
                }
                else {
                    lang.returnMessage(sender, "listall");
                    for (int i = 0; i < net.listAll().size(); i++) {
                        returnMessage(sender, net.listAll().get(i).getID().toString());
                    }
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("component")) {

                Location pos = new Location(0, 0, 0, "world");
                Network network;

                if (args.length < 2) {
                    lang.returnMessage(sender, "component.notype");
                    return true;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    network = net.getSelectedNetwork(player);

                    if (network == null) {
                        lang.returnMessage(sender, "select.noselection");
                    }

                    if (args[1].equalsIgnoreCase("add")) {

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
                            net.selectItem(player, args[3].toUpperCase());
                            lang.returnMessage(sender, "component.select");
                        } else if (args[2].equalsIgnoreCase("misc")) {
                            net.selectComponentType(player, "misc_container");
                            lang.returnMessage(sender, "component.select");
                        }
                    }

                    else if (args[1].equalsIgnoreCase("edit")) {

                    }

                    else if (args[1].equalsIgnoreCase("remove")) {
                        network.removeComponent(pos);
                    }

                    else {
                        returnMessage(sender, "component.noaction");
                    }
                }

                else {
                    if (args.length < 5) {
                        lang.returnMessage(sender, "component.nolocation");
                    }
                    pos.setX(Integer.parseInt(args[2]));
                    pos.setY(Integer.parseInt(args[3]));
                    pos.setZ(Integer.parseInt(args[4]));
                    pos.setDim(args[5]);

                    network = net.getConsoleSelection();

                    if (network == null) {
                        lang.returnMessage(sender, "select.noselection");
                    }

                    if (args[1].equalsIgnoreCase("input")) {
                        network.addInputChest(pos);
                        lang.returnMessage(sender, "component.input.add", network, pos);
                        return true;
                    }

                    else if (args[1].equalsIgnoreCase("sorting")) {
                        if (args.length < 6) {
                            lang.returnMessage(sender, "component.item.noitem");
                        }
                        network.addItemChest(pos, args[6].toUpperCase());
                        lang.returnMessage(sender, "component.item.add", network, pos);
                        return true;
                    }

                    else if (args[1].equalsIgnoreCase("misc")) {
                        network.addMiscChest(pos, false);
                        lang.returnMessage(sender, "component.misc.add", network, pos);
                        return true;

                    }
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("sort")) {
                getSelected(sender).sortAll();
                return true;
            }

            else {
                lang.returnMessage(sender, "invalid");
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
        //("execute as " + player.getUniqueId() + " run tellraw @s " + message);
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
