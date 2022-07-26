package mc.portalcraft.autosort.container;

import org.bukkit.Location;

public class MiscContainer extends BaseContainer{
    private Location pos;

    public MiscContainer(Location pos) {
        this.pos = pos;
    }

    public Location getPos() {
        return pos;
    }
}
