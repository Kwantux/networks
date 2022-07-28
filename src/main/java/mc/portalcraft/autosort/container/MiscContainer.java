package mc.portalcraft.autosort.container;

import mc.portalcraft.autosort.utils.Location;

public class MiscContainer extends BaseContainer{

    boolean takeOverflow;
    public MiscContainer(Location pos, boolean takeOverflow) {
        this.setPos(pos);
        this.takeOverflow = takeOverflow;
    }
}
