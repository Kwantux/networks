package com.quantum625.networks.component;

import com.quantum625.networks.utils.Location;

public class MiscContainer extends BaseComponent {

    boolean takeOverflow;
    public MiscContainer(Location pos, boolean takeOverflow) {
        super(pos);
        this.takeOverflow = takeOverflow;
    }
}
