package com.quantum625.autosort.container;

import com.quantum625.autosort.utils.Location;

public class MiscContainer extends BaseContainer{

    boolean takeOverflow;
    public MiscContainer(Location pos, boolean takeOverflow) {
        super(pos);
        this.takeOverflow = takeOverflow;
    }
}
