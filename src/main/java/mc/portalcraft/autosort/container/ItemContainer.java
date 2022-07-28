package mc.portalcraft.autosort.container;

import mc.portalcraft.autosort.utils.Location;
import org.bukkit.Material;

public class ItemContainer extends BaseContainer{
    private String item;

    public ItemContainer(Location pos, String item) {
        this.setPos(pos);
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}
