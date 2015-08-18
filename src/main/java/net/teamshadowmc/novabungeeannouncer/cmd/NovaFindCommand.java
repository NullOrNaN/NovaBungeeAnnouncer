package net.teamshadowmc.novabungeeannouncer.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.teamshadowmc.novabungeeannouncer.NovaBungeeAnnouncer;

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

        try {
          NovaBungeeAnnouncer.instance.load();
          TextComponent message = new TextComponent("NovaBungeAnnouncer reloaded.");
          message.setColor(ChatColor.GOLD);
          sender.sendMessage(message);
        } catch (Exception ex) {
          ex.printStackTrace();
          TextComponent message = new TextComponent("NovaBungeAnnouncer failed to reload.");
          message.setColor(ChatColor.RED);
          sender.sendMessage(message);
        }
	}
	
  }
}