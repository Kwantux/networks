package de.kwantux.networks.component.module;

public interface ActiveModule extends BaseModule {

    /**
     * Returns the range LEVEL of the component
     * NOT the actual range in blocks
     */
    int range();

}
