package net.quantum625.networks.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.quantum625.config.ConfigurationManager;
import net.quantum625.config.lang.LanguageController;
import net.quantum625.manual.ManualManager;
import net.quantum625.networks.Main;
import net.quantum625.networks.Network;
import net.quantum625.networks.NetworkManager;
import net.quantum625.networks.component.InputContainer;
import net.quantum625.networks.component.MiscContainer;
import net.quantum625.networks.component.SortingContainer;
import net.quantum625.networks.inventory.InventoryMenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class NetworksCommand extends CommandHandler {

    LanguageController lang;
    NetworkManager net;

    public NetworksCommand(Main plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
        net = plugin.getNetworkManager();
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .handler(this::help)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("help")
                .handler(this::help)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("manual")
                .handler(this::manual)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("version")
                .handler(this::version)
        );
        //TODO: Help to specific commands
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("create")
                .argument(StringArgument.of("id"))
                .senderType(Player.class)
                .handler(this::create)
                .permission("networks.create")
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("delete")
                .argument(NetworkArgument.of("network"))
                .senderType(Player.class)
                .handler(this::deleteAsk)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("delete")
                .argument(NetworkArgument.of("network"))
                .literal("confirm")
                .senderType(Player.class)
                .handler(this::delete)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("select")
                .argument(NetworkArgument.of("network"))
                .senderType(Player.class)
                .handler(this::select)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("rename")
                .argument(NetworkArgument.of("network"))
                .argument(StringArgument.of("newID"))
                .handler(this::rename)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("list")
                .senderType(Player.class)
                .handler(this::list)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("list")
                .argument(PlayerArgument.of("player"))
                .permission("networks.listforeign")
                .handler(this::listForeign)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("listall")
                .permission("networks.listforeign")
                .handler(this::listAll)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("info")
                .handler(this::info)
        );

        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("user")
                .literal("add")
                .argument(PlayerArgument.of("player"))
                .handler(this::addUser)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("user")
                .literal("remove")
                .argument(PlayerArgument.of("player"))
                .handler(this::removeUser)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("owner")
                .argument(PlayerArgument.of("player"))
                .handler(this::owner)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("merge")
                .argument(NetworkArgument.of("final"))
                .argument(NetworkArgument.of("other"))
                .handler(this::merge)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("items")
                .handler(this::items)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("view")
                .senderType(Player.class)
                .permission("networks.itemview")
                .handler(this::view)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("save")
                .permission("networks.data")
                .handler(this::saveNetworks)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("save")
                .literal("networks")
                .permission("networks.data")
                .handler(this::saveNetworks)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("save")
                .literal("config")
                .permission("networks.data")
                .handler(this::saveConfig)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("reload")
                .permission("networks.data")
                .handler(this::reloadConfig)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("reload")
                .literal("networks")
                .permission("networks.data")
                .handler(this::reloadNetworks)
        );
        commandManager.command(commandManager.commandBuilder("networks", "network", "net")
                .literal("data")
                .literal("reload")
                .literal("config")
                .permission("networks.data")
                .handler(this::reloadConfig)
        );
    }


    private @Nullable Network selection(CommandSender sender) {
        Network network = net.getSelectedNetwork(sender);
        if (network == null) {
            lang.message(sender, "select.noselection");
            return null;
        }
        return network;
    }


    private void help(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        boolean admin = true;
        if (sender instanceof Player player) {
            admin = player.hasPermission("networks.admin");
        }
        if (admin) lang.message(sender, "help.admin");
        else lang.message(sender, "help");
    }

    private void manual(CommandContext<CommandSender> context) {
        ManualManager.open((Player) context.getSender(), "networks.main");
    }

    private void version(CommandContext<CommandSender> context) {
        lang.message(context.getSender(), "version", plugin.getPluginMeta().getVersion());
    }

    private void saveNetworks(CommandContext<CommandSender> context) {
        net.saveData();
        lang.message(context.getSender(), "data.save.networks");
    }

    private void reloadNetworks(CommandContext<CommandSender> context) {
        net.loadData();
        lang.message(context.getSender(), "data.reload.networks");
    }

    private void saveConfig(CommandContext<CommandSender> context) {
        ConfigurationManager.saveAll();
        lang.message(context.getSender(), "data.save.config");
    }

    private void reloadConfig(CommandContext<CommandSender> context) {
        ConfigurationManager.reloadAll();
        lang.message(context.getSender(), "data.reload.config");
    }

    private void create(CommandContext<CommandSender> context) {

        String id = context.get("id");
        Player player = (Player) context.getSender();

        if (net.getFromID(id) != null) {
            lang.message(player, "create.exists");
        }

        else {
            net.add(id, player.getUniqueId());
            lang.message(player, "create.success", id);
            net.selectNetwork(player, net.getFromID(id));
            lang.message(player, "select.success", id);
        }
    }

    private void deleteAsk(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();

        if (net.checkNetworkPermission(player, network) < 2) {
            lang.message(player, "permission.owner");
            return;
        }

        lang.message(player, "delete.confirm", network.getID());

    }

    private void delete(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();

        if (net.checkNetworkPermission(player, network) < 2) {
            lang.message(player, "permission.owner");
            return;
        }

        net.delete(network.getID());
        lang.message(player, "delete.success", network.getID());
    }

    private void select(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();

        if (net.checkNetworkPermission(player, network) > 0) {
            net.selectNetwork(player, network);
            lang.message(player, "select.success", network.getID());
        }

        else {
            lang.message(player, "permission.user");
        }
    }

    private void rename(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        String newID = context.get("newID");
        String oldID = network.getID();
        CommandSender sender = context.getSender();

        if (net.checkNetworkPermission(sender, network) > 1) {
            if (net.rename(oldID, newID)) lang.message(sender, "rename.success", network.getID());
            else lang.message(sender, "rename.taken", newID);
        }

        else {
            lang.message(sender, "permission.owner");
        }
    }

    private void list(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        List<Network> list = net.listFromUser(player.getUniqueId());
        List<Network> owned = net.listFromUser(player.getUniqueId());



        if (list.size() == 0) {
            lang.message(player, "list.empty");
        }

        else {
            if (owned.size() > 1) {
                lang.message(player, "list.owned");
                for (Network network : owned) {
                    player.sendMessage(network.displayText());
                    list.remove(network);
                }
            }
            if (list.size() > 1) lang.message(player, "list");
            for (Network network : list) {
                player.sendMessage(network.displayText());
            }
        }

    }

    private void listAll(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if (net.listAll().size() == 0) {
            lang.message(sender, "list.all.empty");
            return;
        }
        lang.message(sender, "list.all");
        for (Network network : net.listAll()) {
            sender.sendMessage(network.displayText());
        }
    }

    private void listForeign(CommandContext<CommandSender> context) {

        Player player = context.get("player");
        CommandSender sender = context.getSender();
        List<Network> list = net.listFromUser(player.getUniqueId());
        List<Network> owned = net.listFromOwner(player.getUniqueId());


        if (list.size() == 0) {
            lang.message(sender, "list.foreign.empty", player.displayName());
        }

        else {
            if (owned.size() > 1) {
                lang.message(sender, "list.foreign.owned", player.displayName());
                for (Network network : owned) {
                    sender.sendMessage(network.displayText());
                    list.remove(network);
                }
            }
            lang.message(sender, "list.foreign", player.displayName());
            for (Network network : list) {
                sender.sendMessage(network.displayText());
            }
        }
    }

    private void info(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Network network = selection(sender);
        if (network == null) return;
        lang.message(sender, "info.title");
        lang.message(sender, "info.name", network.getID());
        lang.message(sender, "info.owner", Bukkit.getOfflinePlayer(network.getOwner()).getName());

        StringBuilder users = new StringBuilder();

        for (UUID uid : network.getUsers()) {
            users.append(Bukkit.getOfflinePlayer(uid).getName()).append(",  ");
        }

        if (users.length() > 0) {
            users = new StringBuilder(users.substring(0, users.length() - 3));
        }

        lang.message(sender, "info.users", users.toString());

        lang.message(sender, "info.components.total", String.valueOf(network.getAllComponents().size()));
        lang.message(sender, "info.range", String.valueOf(network.getMaxRange()));

        lang.message(sender, "info.components.input");
        for (InputContainer container : network.getInputChests()) {
            sender.sendMessage(Component.text("    " + container.getPos().toString()));
        }
        lang.message(sender, "info.components.sorting");
        for (SortingContainer container : network.getSortingChests()) {
            Component items = Component.empty();
            for (String mat : container.getItems()) {
                items.append(Component.translatable(Material.valueOf(mat).translationKey()));
            }
            sender.sendMessage(Component.text("    " + container.getPos().toString()).hoverEvent(HoverEvent.showText(items)));
        }
        lang.message(sender, "info.components.misc");
        for (MiscContainer container : network.getMiscChests()) {
            sender.sendMessage(Component.text("    " + container.getPos().toString()));
        }
    }

    private void addUser(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (net.checkNetworkPermission(sender, selection(sender)) < 2) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (network.getUsers().contains(target.getUniqueId())) {
            lang.message(sender, "user.add.nochange", Component.text(network.getID()), target.displayName());
            return;
        }

        network.addUser(target.getUniqueId());
        lang.message(sender, "user.add", Component.text(network.getID()), target.displayName());

    }

    private void removeUser(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (net.checkNetworkPermission(sender, selection(sender)) < 2) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (!network.getUsers().contains(target.getUniqueId())) {
            lang.message(sender, "user.remove.nochange", Component.text(network.getID()), target.displayName());
            return;
        }

        network.removeUser(target.getUniqueId());
        lang.message(sender, "user.remove", Component.text(network.getID()), target.displayName());

    }

    private void owner(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (net.checkNetworkPermission(sender, selection(sender)) < 2) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (target.getUniqueId().equals(network.getOwner())) {
            lang.message(sender, "user.owner.nochange", Component.text(network.getID()), target.displayName());
            return;
        }

        network.removeUser(target.getUniqueId());
        network.addUser(network.getOwner());
        network.setOwner(target.getUniqueId());
        lang.message(sender, "user.owner", Component.text(network.getID()), target.displayName());

    }


    private void merge(CommandContext<CommandSender> context) {
        Network finalNetwork = context.get("final");
        Network otherNetwork = context.get("other");
        CommandSender sender = context.getSender();

        if (net.checkNetworkPermission(sender, finalNetwork) < 2 || net.checkNetworkPermission(sender, otherNetwork) < 2) {
            lang.message(sender, "merge.nopermission");
            return;
        }

        if (finalNetwork.equals(otherNetwork)) {
            lang.message(sender, "merge.identical");
        }
        net.delete(otherNetwork.getID());
        lang.message(sender, "merge.success", Component.text(finalNetwork.getID()), Component.text(otherNetwork.getID()));
    }

    private void items(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Network network = selection(sender);

        if (net.checkNetworkPermission(sender, network) < 1) {
            lang.message(sender, "permission.user");
            return;
        }

        if (network.countItems().entrySet().size() == 0) {
            lang.message(sender, "items.noitems");
            return;
        }

        lang.message(sender, "items.message", Component.text(network.getID()));

        for (Map.Entry<Material, Integer> entry : network.countItems().entrySet()) {
            sender.sendMessage(Component.translatable(entry.getKey().translationKey()).append(Component.text(":  ").color(TextColor.color(0, 255, 230)).append(Component.text(entry.getValue())).color(TextColor.color(255, 255, 255))).hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.of(entry.getKey().key(), entry.getValue()))));
        }
    }

    private void view(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Network network = selection(sender);
        InventoryMenuManager.addInventoryMenu((Player) sender, network);

    }


}
