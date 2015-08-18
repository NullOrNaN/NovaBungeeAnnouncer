package net.teamshadowmc.novabungeeannouncer.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.teamshadowmc.novabungeeannouncer.NovaBungeeAnnouncer;

public class MigrateConfig extends Command {
    NovaBungeeAnnouncer plugin;

    public MigrateConfig(String name, NovaBungeeAnnouncer p) {
        super(name);
        plugin = p;
    }
    public void execute(final CommandSender sender, String[] args) {

    }
}
