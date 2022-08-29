package com.quantum625.autosort.container;

import com.quantum625.autosort.utils.Location;

public class ItemContainer extends BaseContainer{
    private String item;

    public ItemContainer(Location pos, String item) {
        super(pos);
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}
