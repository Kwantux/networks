package dev.nanoflux.networks.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import dev.nanoflux.networks.inventory.InventoryMenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import dev.nanoflux.config.ConfigurationManager;
import dev.nanoflux.config.lang.LanguageController;
import dev.nanoflux.manual.ManualManager;
import dev.nanoflux.networks.Main;
import dev.nanoflux.networks.Config;
import dev.nanoflux.networks.Network;
import dev.nanoflux.networks.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class NetworksCommand extends CommandHandler {

    LanguageController lang;
    Manager manager;
    Config config;

    public NetworksCommand(Main plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        lang = plugin.getLanguage();
        manager = plugin.getNetworkManager();
        config = plugin.getConfiguration();
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
                .literal("force")
                .permission("networks.force")
                .handler(this::force)
        );
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
                .handler(this::getNetworks)
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
        Network network = manager.selection(sender);
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
        if (admin) lang.message(sender, "help.admin", plugin.getPluginMeta().getVersion());
        else lang.message(sender, "help", plugin.getPluginMeta().getVersion());
    }

    private void manual(CommandContext<CommandSender> context) {
        ManualManager.open((Player) context.getSender(), "networks.main");
    }

    private void version(CommandContext<CommandSender> context) {
        lang.message(context.getSender(), "version", plugin.getPluginMeta().getVersion());
    }

    private void force(CommandContext<CommandSender> context) {
        manager.forceToggle((Player) context.getSender());
        lang.message(context.getSender(), "force");
    }

    private void saveNetworks(CommandContext<CommandSender> context) {
        manager.saveData();
        lang.message(context.getSender(), "data.save.networks");
    }

    private void reloadNetworks(CommandContext<CommandSender> context) {
        manager.loadData();
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

        if (!(manager.withOwner(player.getUniqueId()).size() < config.getMaxNetworks() || player.hasPermission("networks.bypass_limit"))) {
            lang.message(context.getSender(), "create.limit", player.displayName());
            return;
        }

        if (manager.getFromName(id) != null) {
            lang.message(player, "create.exists");
            return;
        }

        manager.create(id, player.getUniqueId());
        lang.message(player, "create.success", id);
        manager.select(player, manager.getFromName(id));
        lang.message(player, "select.success", id);
    }

    private void deleteAsk(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();

        if (!manager.permissionOwner(player,network)) {
            lang.message(player, "permission.owner");
            return;
        }

        lang.message(player, "delete.confirm", network.name());

    }

    private void delete(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();

        if (!manager.permissionOwner(player,network)) {
            lang.message(player, "permission.owner");
            return;
        }

        manager.delete(network.name());
        lang.message(player, "delete.success", network.name());
    }

    private void select(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        Player player = (Player) context.getSender();
        
        if (manager.permissionUser(player, network)) {
            manager.select(player, network);
            lang.message(player, "select.success", network.name());
        }

        else {
            lang.message(player, "permission.user");
        }
    }

    private void rename(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        String newID = context.get("newID");
        String oldID = network.name();
        CommandSender sender = context.getSender();

        if (manager.permissionOwner(sender, network)) {
            if (manager.rename(oldID, newID)) lang.message(sender, "rename.success", network.name());
            else lang.message(sender, "rename.taken", newID);
        }

        else {
            lang.message(sender, "permission.owner");
        }
    }

    private void list(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        List<Network> list = manager.withUser(player.getUniqueId());
        List<Network> owned = manager.withUser(player.getUniqueId());



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

    private void getNetworks(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if (manager.getNetworks().size() == 0) {
            lang.message(sender, "list.all.empty");
            return;
        }
        lang.message(sender, "list.all");
        for (Network network : manager.getNetworks()) {
            sender.sendMessage(network.displayText());
        }
    }

    private void listForeign(CommandContext<CommandSender> context) {

        Player player = context.get("player");
        CommandSender sender = context.getSender();
        List<Network> list = manager.withUser(player.getUniqueId());
        List<Network> owned = manager.withOwner(player.getUniqueId());


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
        lang.message(sender, "info.name", network.name());
        lang.message(sender, "info.owner", Bukkit.getOfflinePlayer(network.owner()).getName());

        StringBuilder users = new StringBuilder();

        for (UUID uid : network.users()) {
            users.append(Bukkit.getOfflinePlayer(uid).getName()).append(",  ");
        }

        if (users.length() > 0) {
            users = new StringBuilder(users.substring(0, users.length() - 3));
        }

        lang.message(sender, "info.users", users.toString());

        lang.message(sender, "info.components.total", String.valueOf(network.components().size()));
        lang.message(sender, "info.range", String.valueOf(network.properties().baseRange()));
    }

    private void addUser(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!manager.permissionOwner(sender, selection(sender))) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (network.users().contains(target.getUniqueId())) {
            lang.message(sender, "user.add.nochange", Component.text(network.name()), target.displayName());
            return;
        }

        network.addUser(target.getUniqueId());
        lang.message(sender, "user.add", Component.text(network.name()), target.displayName());

    }

    private void removeUser(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!manager.permissionOwner(sender, selection(sender))) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (!network.users().contains(target.getUniqueId())) {
            lang.message(sender, "user.remove.nochange", Component.text(network.name()), target.displayName());
            return;
        }

        network.removeUser(target.getUniqueId());
        lang.message(sender, "user.remove", Component.text(network.name()), target.displayName());

    }

    private void owner(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!(manager.withOwner(target.getUniqueId()).size() < config.getMaxNetworks() || sender.hasPermission("networks.bypass_limit"))) {
            lang.message(context.getSender(), "create.limit", target.displayName());
            return;
        }

        if (!manager.permissionOwner(sender, selection(sender))) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (target.getUniqueId().equals(network.owner())) {
            lang.message(sender, "user.owner.nochange", Component.text(network.name()), target.displayName());
            return;
        }

        network.removeUser(target.getUniqueId());
        network.addUser(network.owner());
        network.owner(target.getUniqueId());
        lang.message(sender, "user.owner", Component.text(network.name()), target.displayName());

    }


    private void merge(CommandContext<CommandSender> context) {
        Network finalNetwork = context.get("final");
        Network otherNetwork = context.get("other");
        CommandSender sender = context.getSender();

        if (!manager.permissionOwner(sender, finalNetwork) || !manager.permissionOwner(sender, otherNetwork)) {
            lang.message(sender, "merge.nopermission");
            return;
        }

        if (finalNetwork.equals(otherNetwork)) {
            lang.message(sender, "merge.identical");
        }
        manager.delete(otherNetwork.name());
        lang.message(sender, "merge.success", Component.text(finalNetwork.name()), Component.text(otherNetwork.name()));
    }

    private void items(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Network network = selection(sender);

        if (!manager.permissionUser(sender, network)) {
            lang.message(sender, "permission.user");
            return;
        }

        HashMap<Material, Integer> materials = network.materials();

        if (materials.size() == 0) {
            lang.message(sender, "items.noitems");
            return;
        }

        lang.message(sender, "items.message", Component.text(network.name()));

        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            sender.sendMessage(Component.translatable(entry.getKey().translationKey()).append(Component.text(":  ").color(TextColor.color(0, 255, 230)).append(Component.text(entry.getValue())).color(TextColor.color(255, 255, 255))).hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.of(entry.getKey().key(), entry.getValue()))));
        }
    }

    private void view(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Network network = selection(sender);
        InventoryMenuManager.addInventoryMenu((Player) sender, network);

    }


}
