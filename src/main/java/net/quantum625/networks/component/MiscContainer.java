package net.quantum625.networks.component;

import net.quantum625.networks.utils.Location;

public class MiscContainer extends BaseOutputContainer {

    public MiscContainer(Location pos) {
        super(pos);
    }

    @Override
    public String getType() {return "misc";}
}
