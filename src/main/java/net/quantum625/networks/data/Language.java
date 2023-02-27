package net.quantum625.networks.data;

import net.quantum625.networks.Installer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Language extends BaseConfiguration{
    String lang_id;

    public Language(JavaPlugin plugin, Installer installer, String lang_id) {
        super(plugin, "lang/"+lang_id+".yml", installer);
        keys = essentialKeys;
        this.lang_id = lang_id;
        Bukkit.getLogger().info("[Networks] Launched using language module " + lang_id);
    }

    private String[] essentialKeys = {
            "data.load", "data.save",
            "create.success", "create.success.eco", "create.exists", "create.noname", "create.fail", "create.noowner", "create.nomoney",
            "delete.success", "delete.success.eco", "delte.confirm", "delete.fail", "delete.nonetwork",
            "select.noselection", "select.success", "select.notexisting", "select.nonetwork",
            "permission.server", "permission.owner", "permission.user",
            "list.empty", "list.noplayer", "list",
            "component.input.add", "component.sorting.add", "component.sorting.noitem", "component.sorting.setitem", "component.sortign.removeitem", "component.misc.add",
            "component.select", "component.remove", "component.noaction", "component.notype", "component.limit", "component.invalid_block", "component.nocomponent", "component.priority",
            "upgrade.noprice", "upgrade.noeconomy", "upgrade.maxed", "upgrade.disabled", "upgrade.nomoney", "upgrade.success",
            "rangeupgrade.last", "rangeupgrade.alreadyupgraded", "rangeupgrade.unlockfirst", "rangeupgrade.success",
            "location.none", "location.occupied",
            "user.specify", "user.action", "user.add", "user.remove",
            "merge.identical", "merge.invalid", "merge.nopermission", "merge.nonetwork", "merge.success",
            "info.input", "info.sorting", "info.misc",
            "value.notpositive",
            "item.name.wand.0", "item.name.wand.1", "item.name.input", "item.name.sorting", "item.name.misc", "item.name.upgrade",
            "item.lore.wand.0", "item.lore.wand.1", "item.lore.input", "item.lore.sorting", "item.lore.misc", "item.lore.upgrade",
            "notice",
            "invalid"
    };

    public String getText(String id) {
        if (config.get(id) == null) {
            return null;
        }
        return config.get(id).toString();
    }

}
