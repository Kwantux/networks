package com.quantum625.networks.component;

import com.quantum625.networks.utils.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemContainer extends BaseComponent {
    private String[] items;

    public ItemContainer(Location pos, String[] items) {
        super(pos);
        this.items = items;
    }

    public void addItem(String item) {
        String[] items2 = new String[items.length+1];
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(item)) {
                return;
            }
            items2[i] = items[i];
        }
        items2[items.length] = item;
        items = items2;
    }

    public boolean containsItem(String item) {
        for (String i : items) {
            if (i.equalsIgnoreCase(item)) return true;
        }
        return false;
    }
    public void removeItem(String item) {
        if (containsItem(item)) {
            String[] items2 = new String[items.length-1];
            int shift = 0;
            for (int i = 0; i < items.length; i++) {
                if (!items[i].equalsIgnoreCase(item)) {
                    items2[i-1] = items[i];
                }
                else {
                    shift = 1;
                }
            }
            items2[items.length] = item;
            items = items2;
        }
    }
    public String[] getItems() {
        return items;
    }
}
