package net.teamshadowmc.novabungeeannouncer.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.teamshadowmc.novabungeeannouncer.NovaBungeeAnnouncer;
import net.teamshadowmc.novabungeeannouncer.utils.Utils;
import net.teamshadowmc.novabungeeannouncer.utils.NBAConfig;

import java.util.Collection;

public class NBASet extends Command {

    private NovaBungeeAnnouncer plugin;
    private Utils util;
    private NBAConfig config;
    private boolean debug = true;


    public NBASet(String name, NovaBungeeAnnouncer p)
    {
        super(name);
        plugin = p;
        loadUtils();
        loadCfg();
    }
    public void loadUtils() { util = new Utils(this.plugin); }
    public void loadCfg() {
        try {
            config = new NBAConfig(this.plugin);
            config.loadCfg();

            Object dbgSect = config.getConfig().get("debug");
            if (dbgSect != null &&  dbgSect instanceof Collection) {
                this.debug = config.getConfig().getBoolean("debug.enabled", true);
            } else {
                try {
                    boolean tempBool = true;
                    String tempStr = "medium";
                    config.getConfig().set("debug.enabled", tempBool);
                    config.getConfig().set("debug.level", tempStr);
                } catch (Exception ex) {

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void execute(final CommandSender sender, String[] args) {
        if (sender.hasPermission("nba.config")) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer pp = (ProxiedPlayer) sender;
            }
            String configItem = args[0];
            String value = args[1];
            switch (configItem.toLowerCase()) {
                case "debug":
                    if (isBool(value.toLowerCase())) {
                        boolean dbgValue = Boolean.parseBoolean(value.toLowerCase());
                        String msg = String.format("Set %s to %d", "debug.enabled", dbgValue);
                        normMsg(sender, msg);
                    } else {
                        errMsg(sender, "Please supply the value as a boolean (true/false)");
                    }
                    break;

                case "debuglevel":
                    if (value.equalsIgnoreCase("high") || value.equalsIgnoreCase("medium") || value.equalsIgnoreCase("low")) {
                        config.getConfig().set("debug.level", value.toLowerCase());
                        String msg = String.format("Set %s to %d", "debug.level", value.toLowerCase());
                        normMsg(sender, msg);
                    } else {
                        errMsg(sender, "Valid values are 'high', 'medium', and 'low'.");
                    }
                    break;

                default:
                    errMsg(sender, "Invalid or unsusported setting! Please see our wiki page to use this command.");
                    break;
            }
        }
    }
    private boolean isBool(String testString) { return (Boolean.parseBoolean(testString)); }

    public void normMsg(CommandSender to, String msg) {
        if (to instanceof ProxiedPlayer) {
            TextComponent message = new TextComponent(msg);
            to.sendMessage(message);
        }
        else {
            util.log(String.format("[CMD] %s",msg));
        }
    }

    public void errMsg(CommandSender to, String msg) {
        if (to instanceof  ProxiedPlayer) {
            TextComponent message = new TextComponent(msg);
            message.setColor(ChatColor.RED);
        }
        else {
            util.log(String.format("[CMD] %s",msg));
        }
    }

}
