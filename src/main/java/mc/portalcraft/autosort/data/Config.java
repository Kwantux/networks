package mc.portalcraft.autosort.data;

import mc.portalcraft.autosort.Autosort;

public class Config extends BaseFile{
    public Config(Autosort main) {
        super(main, "config.yml");
        setLanguage("de");
    }

    public void setLanguage(String language) {
        config.set("lang", language);
    }
}
