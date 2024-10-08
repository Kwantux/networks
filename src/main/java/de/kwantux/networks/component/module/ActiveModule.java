package de.kwantux.networks.component.module;

public interface ActiveModule extends BaseModule {

    /**
     * Returns the range LEVEL of the component
     * NOT the actual range in blocks
     */
    int range();

    /**
     * Increases the range LEVEL of the component
     * NOT the actual range in blocks
     * Does not check if the range is already at maximum
     * The check needs to be implemented when calling this method
     */
    void rangeUp();

}
