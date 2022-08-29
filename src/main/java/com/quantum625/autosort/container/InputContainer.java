package com.quantum625.autosort.container;

import com.quantum625.autosort.utils.Location;

public class InputContainer extends BaseContainer{

    private int tickrate = 20;
    public InputContainer(Location pos, int tickrate) {
        super(pos);
        this.tickrate = tickrate;
    }
    
    public void setTickrate(int t) {
        this.tickrate = t;
    }

    public int getTickrate() {
        return tickrate;
    }
}
