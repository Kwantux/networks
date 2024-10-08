package de.kwantux.networks.api;

import de.kwantux.networks.storage.InterThreadTransmissionController;

public class APIHolder {

    static InterThreadTransmissionController ittc = null;

    public static InterThreadTransmissionController getITTC() {
        return ittc;
    }

    public static void setITTC(InterThreadTransmissionController ittc) {
        APIHolder.ittc = ittc;
    }

    static Storage storage = null;

    public static Storage getStorage() {
        return storage;
    }

    public static void setStorage(Storage storage) {
        APIHolder.storage = storage;
    }

    static Manager manager = null;

    public static Manager getManager() {
        return manager;
    }

    public static void setManager(Manager manager) {
        APIHolder.manager = manager;
    }
}
