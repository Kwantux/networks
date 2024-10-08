package de.kwantux.networks.commands;

import de.kwantux.config.ConfigurationManager;
import de.kwantux.config.util.exceptions.InvalidNodeException;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.component.component.InputContainer;
import de.kwantux.networks.component.component.MiscContainer;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.config.Config;
import de.kwantux.networks.inventory.InventoryMenuManager;
import de.kwantux.networks.utils.BlockLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import de.kwantux.manual.ManualManager;
import de.kwantux.networks.Main;
import de.kwantux.networks.Network;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.setting.ManagerSetting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static de.kwantux.networks.Main.*;
import static de.kwantux.networks.commands.ComponentTypeParser.componentTypeParser;
import static de.kwantux.networks.commands.NetworkParser.networkParser;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.bukkit.parser.location.LocationParser.locationParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class NetworksCommand extends CommandHandler {

    public NetworksCommand(Main plugin, CommandManager<CommandSender> commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        cmd.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);

        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .handler(this::help)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("help")
                .handler(this::help)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("manual")
                .handler(this::manual)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("version")
                .handler(this::version)
        );
        //TODO: Help to specific commands
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("force")
                .permission("networks.force")
                .handler(this::force)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("create")
                .required("id", stringParser())
                .senderType(Player.class)
                .handler(this::create)
                .permission("networks.create")
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("delete")
                .required("network", networkParser())
                .senderType(Player.class)
                .handler(this::deleteAsk)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("delete")
                .required("network", networkParser())
                .literal("confirm")
                .senderType(Player.class)
                .handler(this::delete)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("select")
                .required("network", networkParser())
                .senderType(Player.class)
                .handler(this::select)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("rename")
                .required("network", networkParser())
                .required("newID", stringParser())
                .handler(this::rename)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("list")
                .senderType(Player.class)
                .handler(this::list)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("list")
                .required("player", playerParser())
                .permission("networks.listforeign")
                .handler(this::listForeign)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("listall")
                .permission("networks.listforeign")
                .handler(this::getNetworks)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("info")
                .handler(this::info)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("component")
                .literal("list")
                .handler(this::components)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("component")
                .literal("info")
                .required("location", locationParser())
                .handler(this::componentInfo)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("user")
                .literal("add")
                .required("player", playerParser())
                .handler(this::addUser)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("user")
                .literal("remove")
                .required("player", playerParser())
                .handler(this::removeUser)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("owner")
                .required("player", playerParser())
                .handler(this::owner)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("accept")
                .required("network", networkParser())
                .handler(this::acceptTransfer)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("merge")
                .required("final", networkParser())
                .required("other", networkParser())
                .handler(this::merge)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("items")
                .handler(this::items)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("save")
                .permission("networks.data")
                .handler(this::saveNetworks)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("save")
                .literal("networks")
                .permission("networks.data")
                .handler(this::saveNetworks)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("save")
                .literal("config")
                .permission("networks.data")
                .handler(this::saveConfig)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("reload")
                .permission("networks.data")
                .handler(this::reloadConfig)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("reload")
                .literal("networks")
                .permission("networks.data")
                .handler(this::reloadNetworks)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("data")
                .literal("reload")
                .literal("config")
                .permission("networks.data")
                .handler(this::reloadConfig)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("give")
                .literal("wand")
                .permission("networks.give")
                .senderType(Player.class)
                .handler(this::giveWand)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("give")
                .literal("component")
                .required("type", componentTypeParser())
                .permission("networks.give")
                .senderType(Player.class)
                .handler(this::giveComponent)
        );
        cmd.command(cmd.commandBuilder("networks", Config.commands)
                .literal("give")
                .literal("upgrade")
                .literal("range")
                .required("tier", integerParser(1, cfg.getMaxRanges().length))
                .permission("networks.give")
                .senderType(Player.class)
                .handler(this::giveUpgradeRange)
        );
    }


    private @Nullable Network selection(CommandSender sender) {
        Network network = mgr.selection(sender);
        if (network == null) {
            lang.message(sender, "select.noselection");
            return null;
        }
        return network;
    }

    private void help(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        boolean admin = true;
        if (sender instanceof Player player) {
            admin = player.hasPermission("networks.admin");
        }
        if (admin) lang.message(sender, "help.admin", plugin.getPluginMeta().getVersion());
        else lang.message(sender, "help", plugin.getPluginMeta().getVersion());
    }

    private void manual(CommandContext<CommandSender> context) {
        ManualManager.open((Player) context.sender(), "networks.main");
    }

    private void version(CommandContext<CommandSender> context) {
        lang.message(context.sender(), "version", plugin.getPluginMeta().getVersion());
    }

    private void force(CommandContext<CommandSender> context) {
        boolean active = mgr.forceToggle((Player) context.sender());
        if (active) lang.message(context.sender(), "force.enable");
        else lang.message(context.sender(), "force.disable");
    }

    private void saveNetworks(CommandContext<CommandSender> context) {
        mgr.saveData();
        lang.message(context.sender(), "data.save.networks");
    }

    private void reloadNetworks(CommandContext<CommandSender> context) {
        mgr.loadData();
        lang.message(context.sender(), "data.reload.networks");
    }

    private void saveConfig(CommandContext<CommandSender> context) {
        ConfigurationManager.saveAll();
        lang.message(context.sender(), "data.save.config");
    }

    private void reloadConfig(CommandContext<CommandSender> context) {
        ConfigurationManager.reloadAll();
        lang.message(context.sender(), "data.reload.config");
    }

    private void create(CommandContext<Player> context) {

        String id = context.get("id");
        if (!Network.validName(id)) {
            lang.message(context.sender(), "create.illegal_name");
            return;
        }
        Player player = context.sender();

        if (!(mgr.withOwner(player.getUniqueId()).size() < cfg.getMaxNetworks() || player.hasPermission("networks.bypass_limit"))) {
            lang.message(context.sender(), "create.limit", player.displayName());
            return;
        }

        if (mgr.getFromName(id) != null) {
            lang.message(player, "create.exists");
            return;
        }

        if (!mgr.create(id, player.getUniqueId())) {
            lang.message(player, "create.unknown_fail");
            return;
        }
        lang.message(player, "create.success", id);
        mgr.select(player, mgr.getFromName(id));
        lang.message(player, "select.success", id);
    }

    private void deleteAsk(CommandContext<Player> context) {
        Network network = context.get("network");

        if (!mgr.permissionOwner(context.sender() ,network)) {
            lang.message(context.sender(), "permission.owner");
            return;
        }

        lang.message(context.sender(), "delete.confirm", network.name());

    }

    private void delete(CommandContext<Player> context) {
        Network network = context.get("network");
        Player player = context.sender();

        if (!mgr.permissionOwner(player,network)) {
            lang.message(player, "permission.owner");
            return;
        }

        mgr.delete(network.name());
        lang.message(player, "delete.success", network.name());
    }

    private void select(CommandContext<Player> context) {
        Network network = context.get("network");
        Player player = context.sender();
        
        if (mgr.permissionUser(player, network)) {
            mgr.select(player, network);
            lang.message(player, "select.success", network.name());
        }

        else {
            lang.message(player, "permission.user");
        }
    }

    private void rename(CommandContext<CommandSender> context) {
        Network network = context.get("network");
        String newID = context.get("newID");
        if (!Network.validName(newID)) {
            lang.message(context.sender(), "create.illegal_name");
            return;
        }
        String oldID = network.name();
        CommandSender sender = context.sender();

        if (mgr.permissionOwner(sender, network)) {
            if (mgr.rename(oldID, newID)) lang.message(sender, "rename.success", network.name());
            else lang.message(sender, "rename.taken", newID);
        }

        else {
            lang.message(sender, "permission.owner");
        }
    }

    private void list(CommandContext<Player> context) {
        Player player = context.sender();
        List<Network> list = mgr.withUser(player.getUniqueId());
        List<Network> owned = mgr.withUser(player.getUniqueId());



        if (list.isEmpty()) {
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
        CommandSender sender = context.sender();
        if (mgr.getNetworks().isEmpty()) {
            lang.message(sender, "list.all.empty");
            return;
        }
        lang.message(sender, "list.all");
        for (Network network : mgr.getNetworks()) {
            sender.sendMessage(network.displayText());
        }
    }

    private void listForeign(CommandContext<CommandSender> context) {

        Player player = context.get("player");
        CommandSender sender = context.sender();
        List<Network> list = mgr.withUser(player.getUniqueId());
        List<Network> owned = mgr.withOwner(player.getUniqueId());


        if (list.isEmpty()) {
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
        CommandSender sender = context.sender();
        Network network = selection(sender);
        if (network == null) return;
        lang.message(sender, "info.title");
        lang.message(sender, "info.name", network.name());
        lang.message(sender, "info.owner", Bukkit.getOfflinePlayer(network.owner()).getName());

        StringBuilder users = new StringBuilder();

        for (UUID uid : network.users()) {
            users.append(Bukkit.getOfflinePlayer(uid).getName()).append(",  ");
        }

        if (!users.isEmpty()) {
            users = new StringBuilder(users.substring(0, users.length() - 3));
        }

        lang.message(sender, "info.users", users.toString());

        lang.message(sender, "info.components.total", String.valueOf(network.components().size()));
        lang.message(sender, "info.range", String.valueOf(network.properties().baseRange()));
    }

    private void components(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        Network network = selection(sender);
        if (network == null) return;

        if (network.components().isEmpty()) {
            lang.message(sender, "component.list.empty");
            return;
        }

        for (NetworkComponent component : network.components()) {
            System.out.println(component);
            System.out.println(component.type());
            System.out.println(component.pos());
            System.out.println(component.type().tag());

            sender.sendMessage(
                    Objects.requireNonNullElse(lang.getFinal("item.name.component."+component.type().tag()), Component.text("Unknown Component type: " +  component.type().tag()))
                    .append(Component.text(":  ")
                    .append(component.pos().displayText()))
                    .hoverEvent(HoverEvent.showText(componentInfo(network, component)))
            );
        }
    }

    private void componentInfo(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        Network network = selection(sender);
        NetworkComponent component = mgr.getComponent(new BlockLocation((Location) context.get("location")));
        sender.sendMessage(componentInfo(network, component));
    }

    public static Component componentInfo(Network network, @Nullable NetworkComponent component) {
        try {
            return switch (component) {
                case InputContainer container ->
                        lang.get("wand.info.input", network.name(), component.pos().toString(), String.valueOf(container.range()));
                case SortingContainer container ->
                        lang.get("wand.info.sorting", network.name(), component.pos().toString(), String.valueOf(container.acceptorPriority()), Arrays.stream(container.filters()).toList().toString());
                case MiscContainer container ->
                        lang.get("wand.info.misc", network.name(), component.pos().toString(), String.valueOf(container.acceptorPriority()));
                case null, default -> Component.empty();
            };

        } catch (InvalidNodeException e) {
            return Component.empty();
        }
    }

    private void addUser(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!mgr.permissionOwner(sender, selection(sender))) {
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
        CommandSender sender = context.sender();
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!mgr.permissionOwner(sender, selection(sender))) {
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
        CommandSender sender = context.sender();
        boolean request = Config.requestOwnershipTransfers && !sender.hasPermission("network.transfer_without_request");
        Player target = context.get("player");
        Network network = selection(sender);
        if (network == null) return;

        if (!(mgr.withOwner(target.getUniqueId()).size() < cfg.getMaxNetworks() || sender.hasPermission("networks.bypass_limit"))) {
            lang.message(context.sender(), "create.limit", target.displayName());
            return;
        }

        if (!mgr.permissionOwner(sender, selection(sender))) {
            lang.message(sender, "permission.owner");
            return;
        }

        if (target.getUniqueId().equals(network.owner())) {
            lang.message(sender, "user.owner.nochange", Component.text(network.name()), target.displayName());
            return;
        }

        if (request) {
            lang.message(sender, "user.owner.request.donator", Component.text(network.name()), target.displayName());
            lang.message(target, "user.owner.request.acceptor", network.name(), Bukkit.getOfflinePlayer(network.owner()).getName());
            mgr.requestTransfer(network, target);
        }

        else {
            network.removeUser(target.getUniqueId());
            network.addUser(network.owner());
            network.owner(target.getUniqueId());
        }
    }
    

    private void acceptTransfer(CommandContext<CommandSender> context) {
        Player sender = (Player) context.sender();
        Network network = context.get("network");

        if (!mgr.canTransfer(network, sender)) {
            lang.message(sender, "user.owner.norequest");
            return;
        }

        lang.message(sender, "user.owner.accept.acceptor", Component.text(network.name()));
        Player owner = Bukkit.getPlayer(network.owner());
        if (owner != null) lang.message(owner, "user.owner.accept.donator", sender.displayName());

        mgr.acceptTransfer(network);

        network.removeUser(sender.getUniqueId());
        network.addUser(network.owner());
        network.owner(sender.getUniqueId());
    }


    private void merge(CommandContext<CommandSender> context) {
        Network finalNetwork = context.get("final");
        Network otherNetwork = context.get("other");
        CommandSender sender = context.sender();

        if (!mgr.permissionOwner(sender, finalNetwork) || !mgr.permissionOwner(sender, otherNetwork)) {
            lang.message(sender, "merge.nopermission");
            return;
        }

        if (finalNetwork.equals(otherNetwork)) {
            lang.message(sender, "merge.identical");
        }
        mgr.delete(otherNetwork.name());
        lang.message(sender, "merge.success", Component.text(finalNetwork.name()), Component.text(otherNetwork.name()));
    }

    private void items(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        Network network = selection(sender);
        if (network == null) return;

        if (!mgr.permissionUser(sender, network)) {
            lang.message(sender, "permission.user");
            return;
        }

        HashMap<Material, Integer> materials = network.materials();

        if (materials.isEmpty()) {
            lang.message(sender, "items.noitems");
            return;
        }

        lang.message(sender, "items.message", Component.text(network.name()));

        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            sender.sendMessage(Component.translatable(entry.getKey().translationKey()).append(Component.text(":  ").color(TextColor.color(0, 255, 230)).append(Component.text(entry.getValue())).color(TextColor.color(255, 255, 255))).hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.showItem(entry.getKey().key(), 1))));
        }
    }

    private void giveWand(CommandContext<Player> context) {
        Player player = context.sender();
        ItemStack wand = crf.getNetworkWand(0);
        player.getInventory().addItem(wand);
    }

    private void giveUpgradeRange(CommandContext<Player> context) {
        Player player = context.sender();
        try {
            ItemStack upgrade = crf.getRangeUpgrade(context.get("tier"));
            player.getInventory().addItem(upgrade);
        } catch (InvalidNodeException e) {
            throw new RuntimeException(e);
        }
    }

    private void giveComponent(CommandContext<Player> context) {
        Player player = context.sender();
        ComponentType type = context.get("type");
        player.getInventory().addItem(type.item());
    }

    // Currently disabled
    private void view(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        Network network = selection(sender);
        if (network == null) return;
        InventoryMenuManager.addInventoryMenu((Player) sender, network);

    }


}
