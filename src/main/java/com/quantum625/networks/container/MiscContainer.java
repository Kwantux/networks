package com.quantum625.networks.container;

import com.quantum625.networks.utils.Location;

public class MiscContainer extends BaseContainer{

    boolean takeOverflow;
    public MiscContainer(Location pos, boolean takeOverflow) {
        super(pos);
        this.takeOverflow = takeOverflow;
    }
}
