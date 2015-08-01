package net.teamshadowmc.novabungeeannouncer;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NovaFindCommand extends Command
{
  NovaBungeeAnnouncer plugin;

  public NovaFindCommand(String name, NovaBungeeAnnouncer p)
  {
    super(name);
    plugin = p;
  }

  public void execute(final CommandSender sender, String[] args)
  {
	
	if(sender.hasPermission("nba.reload")||(!(sender instanceof ProxiedPlayer))){
		sender.sendMessage("NovaBungeAnnouncer reloaded.");
		NovaBungeeAnnouncer.instance.load();
	}
	
  }
}