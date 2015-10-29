package com.supersourmonkey.novabungeeannouncer.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import com.supersourmonkey.novabungeeannouncer.AnnouncerConfig.Announcement;

import com.supersourmonkey.novabungeeannouncer.NovaBungeeAnnouncer;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
//import net.md_5.bungee.command.ConsoleCommandSender

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