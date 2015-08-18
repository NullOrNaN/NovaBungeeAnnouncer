package net.teamshadowmc.novabungeeannouncer.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.teamshadowmc.novabungeeannouncer.NovaBungeeAnnouncer;
import net.teamshadowmc.novabungeeannouncer.PlayerMessage;
import net.teamshadowmc.novabungeeannouncer.utils.NBAConfig;


public class NBASend extends Command
{
	NovaBungeeAnnouncer plugin;

	public NBASend(String name, NovaBungeeAnnouncer p)
	{
		super(name);
		plugin = p;
	}

	public void execute(final CommandSender sender, String[] args)
	{
		if(sender.hasPermission("nba.send")|| (!(sender instanceof ProxiedPlayer))||NovaBungeeAnnouncer.instance.perms.get(sender.getName()).contains("+nba.send")){
			if(args.length>0){
				if(plugin.config.nonannouncements.containsKey(args[0])){
					NBAConfig.BroadcastMap bm = plugin.config.nonannouncements.get(args[0]);
					NBAConfig.Announcement an = bm.announcement.clone();
					for(int i = 1; i < args.length; i++){
						an.message = an.message.replaceAll("<<"+i+">>", args[i]);
					}
					PlayerMessage.announceAnnouncement(an, "", bm.servers, bm.permission);
				}
			}
		}

	}
}