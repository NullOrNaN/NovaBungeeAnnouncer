
package net.teamshadowmc.novabungeeannouncer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class Utils {
    private Plugin plugin;
    private Level defLevel = Level.INFO;

    private NBAConfig myConfig;
    private boolean debug;
    private String debugLevel;

    public Utils(Plugin plugin) {
        this.plugin = plugin;
        myConfig = new NBAConfig(this.plugin);
        this.debug = myConfig.debug;
        this.debugLevel = myConfig.debugLevel;
    }

    /**
     * Prints a message to the console using the info level
     *
     * @param msg String to log as a message to console
     */
    public void log(String msg) {
        log(defLevel, msg);
    }

    /**
     * Prints a message to the console
     *
     * @param logLevel The log level
     * @param msg String to log as a message to console
     */
    public void log(Level logLevel, String msg) {
        try {
            plugin.getLogger().log(logLevel, msg);
        } catch (Exception ex) { // Seems that we have an invalid Log Level send it with the default level of info
            if (debug)
                plugin.getLogger().log(Level.SEVERE, String.format("[DEBUG] Error logging message: %s", ex.getMessage()));
            plugin.getLogger().log(defLevel, msg);
        }
    }

    /**
     * Returns a boolean based on debug.level's settings
     *
     * @param verboseLevel The level to check
     * @return Returns boolean for the given level
     */
    public boolean verbosity(String verboseLevel) {
        if (!debug) return false;
        else if (verboseLevel.equals("high")) return (debugLevel.equals("high"));
        else if (verboseLevel.equals("medium")) return (debugLevel.equals("high") || debugLevel.equals("medium"));
        else if (verboseLevel.equals("low")) return (debugLevel.equals("high") || debugLevel.equals("medium")) || debugLevel.equals("low");
        return false;
    }


}
