package quantum625.networks.component;

import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.utils.Location;

public class MiscContainer extends BaseOutputContainer {

    public MiscContainer(Location pos) {
        super(pos);
    }

    @Override
    public ComponentType getType() {return ComponentType.MISC;}
}
