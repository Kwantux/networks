package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;

public class Networks extends BaseFile{
    public Networks(Autosort main) {
        super(main, "networks.yml");
        setLanguage("de");
    }

    public void setLanguage(String language) {
        config.set("lang", language);
    }
}
