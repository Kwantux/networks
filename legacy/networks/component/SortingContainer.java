package quantum625.networks.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.utils.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortingContainer extends BaseOutputContainer {
    protected String[] items;

    public SortingContainer(Location pos, String[] items) {
        super(pos);
        List<String> list = new ArrayList<>(Arrays.stream(items).distinct().toList());
        list.remove("");
        this.items = list.toArray(new String[0]);
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

    public int containsItem(String item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(item)) return i;
        }
        return -1;
    }
    public void removeItem(String item) {
        if (containsItem(item) >= 0) {
            String[] items2 = new String[items.length-1];
            for (int i = 0, k = 0; i < items.length; i++) {
                if (i == containsItem(item)) {
                    continue;
                }
                items2[k++] = items[i];
            }
            items = items2;
        }
    }

    public static int[] removeElement(int[] arr, int index) {
        if (arr == null || index < 0
                || index >= arr.length) {

            return arr;
        }

        int[] anotherArray = new int[arr.length - 1];

        for (int i = 0, k = 0; i < arr.length; i++) {

            if (i == index) {
                continue;
            }

            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }

    public String[] getItems() {
        return items;
    }

    @Override
    public ComponentType getType() {return ComponentType.SORTING;}
}
