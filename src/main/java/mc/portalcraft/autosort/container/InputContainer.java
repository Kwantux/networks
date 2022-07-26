package mc.portalcraft.autosort.container;

import org.bukkit.Location;

public class InputContainer extends BaseContainer{
    private Location pos;

    public InputContainer(Location pos) {
        this.pos = pos;
    }

    public Location getPos() {
        return pos;
    }
}
