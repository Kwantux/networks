package mc.portalcraft.autosort.container;

import mc.portalcraft.autosort.utils.Location;

public class InputContainer extends BaseContainer{

    private int tickrate = 20;
    public InputContainer(Location pos) {
        this.setPos(pos);
        this.tickrate = tickrate;
    }
    
    public void setTickrate(int t) {
        this.tickrate = t;
    }

    public int getTickrate() {
        return tickrate;
    }
}
