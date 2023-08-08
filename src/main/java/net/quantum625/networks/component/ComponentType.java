package net.quantum625.networks.component;

public enum ComponentType {

    EMPTY,
    INPUT,
    FILTERED_INPUT,
    SORTING,
    MISC,
    FURNACE,
    REQUEST;

    public static ComponentType get(String componentType) {
        try  {
            return ComponentType.valueOf(componentType.toUpperCase());
        } catch (IllegalArgumentException | EnumConstantNotPresentException e) {
            return null;
        }
    }
}
