package com.quantum625.networks.component;

import com.quantum625.networks.utils.Location;

public class InputContainer extends BaseComponent {

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
