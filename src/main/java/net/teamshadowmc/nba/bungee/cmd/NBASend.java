package net.teamshadowmc.nba.bungee.cmd;

import net.teamshadowmc.nba.bungee.NovaBungeeAnnouncer;
import net.teamshadowmc.nba.bungee.PlayerMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
//import net.md_5.bungee.command.ConsoleCommandSender;

import net.teamshadowmc.nba.bungee.AnnouncerConfig;

public class NBASend extends Command {
	NovaBungeeAnnouncer plugin;

	public NBASend(String name, NovaBungeeAnnouncer p) {
		super(name);
		plugin = p;
	}

	public void execute(final CommandSender sender, String[] args) {
		if(sender.hasPermission("nba.send")|| (!(sender instanceof ProxiedPlayer))||NovaBungeeAnnouncer.instance.perms.get(sender.getName()).contains("+nba.send")){
			if(args.length>0) {
				if(plugin.config.nonannouncements.containsKey(args[0])) {
					AnnouncerConfig.BroadcastMap bm = plugin.config.nonannouncements.get(args[0]);
					AnnouncerConfig.Announcement an = bm.announcement.clone();
					for(int i = 1; i < args.length; i++) {
						an.message = an.message.replaceAll("<<"+i+">>", args[i]);
					}
					PlayerMessage.announceAnnouncement(an, "", bm.servers, bm.permission);
				}
			}
		}

	}
}