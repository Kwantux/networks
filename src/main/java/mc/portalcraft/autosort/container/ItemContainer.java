package mc.portalcraft.autosort.container;

import org.bukkit.Location;
import org.bukkit.Material;

public class ItemContainer extends BaseContainer{
    private Location pos;
    private Material item;

    public ItemContainer(Location pos, Material item) {
        this.pos = pos;
        this.item = item;
    }

    public Location getPos() {
        return pos;
    }

    public Material getItem() {
        return item;
    }
}
