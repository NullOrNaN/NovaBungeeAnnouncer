package net.teamshadowmc.novabungeeannouncer;

import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class ConfigHandler {
    public Plugin plugin = this.plugin;
    public Map yamlConfig;

    public void configReader() {

        String configFile = String.format("%s%s", plugin.getDataFolder(), "NovaBungeeAnnouncer.yml");
        plugin.getLogger().info(configFile);
        Yaml yaml = new Yaml();
        yamlConfig = (Map) yaml.load(configFile);

    }

    public boolean configExists() {
        return false;
    }
}

