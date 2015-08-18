package net.teamshadowmc.novabungeeannouncer.utils;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class NBAConfig {

    /* This is roughly based off a gist by rylinaux: https://gist.github.com/rylinaux/8701108 */

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private Plugin plugin;
    private File file;
    private Configuration configuration;

    /* Utils.class-based vars */
    private Utils utils;
    private boolean utilsLoaded = false;
    private boolean verbHigh;
    private boolean verbMed;
    private boolean verbLow;
    
    /* Enable ALL debugging by default when no config exists */
    public boolean debug = true;
    public String debugLevel = "high";

    public String order = "sequential";
    public String permissionServer = "lobby";
    public int permissionCacheTime = 0;


    public NBAConfig(Plugin plugin) {
        this(plugin,"config.yml");
    }

    public NBAConfig(Plugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name));
    }

    /**
     * Main class handler
     *
     * @param plugin The plugin
     * @param file File value for the config file
     */
    public NBAConfig(Plugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    /**
     *  Loads the config
     */
    public void loadCfg() {
        /* Setup Utility functions */
        if (!utilsLoaded) {
            try {
                utils = new Utils(plugin);
                utilsLoaded = true;

                verbHigh = utils.verbosity("high");
                verbMed = utils.verbosity("medium");
                verbLow = utils.verbosity("low");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (createConfig("config.yml"))
            if (verbHigh) utils.log("[DEBUG] Config created");

        try {
            configuration = provider.load(file);
            /* Update debugging values if successful load */
            this.verbHigh = utils.verbosity("high");
            this.verbMed = utils.verbosity("medium");
            this.verbLow = utils.verbosity("low");
        } catch (Exception ex) {
            if (debug) utils.log(Level.SEVERE, String.format("[DEBUG] Unable to load config file %s.", file.getName()));
            ex.printStackTrace();
        }
    }

    /**
     * Creates a config file if one doesn't exist
     *
     * @param configFile the YAML file's name
     * @return Returns if we were able to create the config or not
     */
    public boolean createConfig(String configFile) {
        if (!plugin.getDataFolder().exists()) {
            if (verbMed) utils.log("[DEBUG] Attempting to create the config folder");
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder(), configFile);
        if (!file.exists()) {
            if (verbHigh) utils.log(String.format("[DEBUG] Attempting to create file %s", configFile));
            try {
                file.createNewFile();
                InputStream in = plugin.getResourceAsStream(configFile);
                OutputStream out = new FileOutputStream(file);
                ByteStreams.copy(in, out);
                if (verbMed) utils.log(String.format("{DEBUG] Created %s", configFile));
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     *  Saves the currently loaded config
     */
    public void saveConfig() {
        if (verbHigh) utils.log("[DEBUG] Trying to save config");
        try {
            provider.save(configuration, file);
            if (verbHigh) utils.log("[DEBUG] Successfully saved the config.");
        } catch (IOException ex) {
            utils.log(Level.SEVERE, String.format("Couldn't save config '%s'.", file.getName()));
        } catch (NullPointerException ex) {
            utils.log(Level.SEVERE, "Got an NPE in saveConfig()");
        }
    }

    public Configuration getConfig() {
        return configuration;
    }

    public File getFile() {
        return file;
    }



    /* This is all from the old config stuff should likely clean up later */
    public class Announcement {
        public String type;
        public String message;

        public Announcement clone(){
            Announcement clone = new Announcement();
            clone.type = type + "";
            clone.message = message +"";
            return clone;
        }
    }

    public HashMap<String, MessageMap> servers = new HashMap<String, MessageMap>();

    public static class MessageMap {
        public ArrayList<String> servers;
        public int offset;
        public int delay;
        public String permission;
        public ArrayList<Announcement> announcements = new ArrayList<Announcement>();
    }

    public HashMap<String, BroadcastMap> nonannouncements = new HashMap<String, BroadcastMap>();

    public static class BroadcastMap  {
        public ArrayList<String> servers;
        public String permission;
        public Announcement announcement;
    }

}