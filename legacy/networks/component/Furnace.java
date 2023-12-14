package quantum625.networks.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.utils.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Furnace extends SortingContainer {

    boolean autoSortResults = true;

    public Furnace(Location pos, String[] fuels, boolean autoSortResults) {
        super(pos, fuels);
        List<String> list = new ArrayList<>(Arrays.stream(fuels).distinct().toList());
        list.remove("");
        this.items = list.toArray(new String[0]);
        this.autoSortResults = autoSortResults;
    }

    public ItemStack getInputSlot() {
        return getInventory().getItem(0);
    }

    public ItemStack getOutputSlot() {
        return getInventory().getItem(2);
    }

    public ItemStack getFuelSlot() {
        return getInventory().getItem(1);
    }
    public void setFuelSlot(ItemStack stack) {
        getInventory().setItem(1, stack);
    }

    @Override
    public ComponentType getType() {return ComponentType.FURNACE;}
}
