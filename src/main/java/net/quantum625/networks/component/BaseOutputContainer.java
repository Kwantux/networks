package net.quantum625.networks.component;

import net.quantum625.networks.utils.Location;

public class BaseOutputContainer extends BaseComponent{

    protected int priority = 0;

    protected BaseOutputContainer(Location location) {
        super(location);
    }

    public int getPriority() {return priority;}
    public void setPriority(int priority) {priority = priority;}
    public void incrementPriority() {priority++;}
    public void decrementPriority() {priority--;}
}
