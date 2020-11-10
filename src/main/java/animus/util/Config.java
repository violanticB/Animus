package animus.util;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author Ethan Borawski
 */
public abstract class Config<T> {

    private final ConfigurationSection section;

    public Config(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public abstract List<T> load();

}
