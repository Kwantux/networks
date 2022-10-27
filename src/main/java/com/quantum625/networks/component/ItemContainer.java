package com.quantum625.networks.component;

import com.quantum625.networks.utils.Location;

public class ItemContainer extends BaseComponent {
    private String[] items;

    public ItemContainer(Location pos, String[] items) {
        super(pos);
        this.items = items;
    }

    public String[] getItems() {
        return items;
    }
}
