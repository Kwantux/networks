package mc.portalcraft.autosort;

import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class CommandListener implements CommandExecutor{

    private NetworkManager net = new NetworkManager();

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

            if (args[0].equalsIgnoreCase("create")) {

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
                    net.add(new StorageNetwork(args[1], owner));
                    if (net.getFromID(args[1]) != null) {
                        returnMessage(sender, "Successfully created network " + args[1]);
                    }
                    else {
                        returnMessage(sender, "Failed creating a storage network");
                    }

                }
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
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

            if (args[0].equalsIgnoreCase("select")) {
                returnMessage(sender, "This feature is not implemented yet!");
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                returnMessage(sender, "This feature is not implemented yet!");
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {

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
                    for (int i = 0; i < net.listFromOwner(owner).size()-1; i++) {
                        returnMessage(sender, net.listFromOwner(owner).get(i).getID().toString());
                    }
                }

                return true;

            }

            if (args[0].equalsIgnoreCase("listall")) {
                for (int i = 0; i < net.listAll().size()-1; i++) {
                    returnMessage(sender, net.listAll().get(i).getID().toString());
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("chest")) {
                returnMessage(sender, "This feature is not implemented yet!");
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

}
