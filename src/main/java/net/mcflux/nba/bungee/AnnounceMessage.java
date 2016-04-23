package net.mcflux.nba.bungee;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
//import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
//import net.md_5.bungee.protocol.packet.Chat;


public class AnnounceMessage implements Runnable{
	public AnnouncerConfig.MessageMap server;
	public String serverName;
	public int nextAnnounce = 0;

	public AnnounceMessage(AnnouncerConfig.MessageMap server, String serverName){
		this.server = server;
		this.serverName = serverName;
	}
	@Override
	public void run() {
		if(server.announcements.size()==0){
			System.out.println("[NovaBungeeAnnouncer] There are currently no messages to announce for the server or set " + serverName + ". Add at least one for messages to send");
		}
		else{
			if(server.announcements.size()<=nextAnnounce){
				nextAnnounce = 0;
			}
			AnnouncerConfig.Announcement toSay = server.announcements.get(nextAnnounce);
			PlayerMessage.announceAnnouncement(toSay, serverName, server.servers, server.permission);
			nextAnnounce++;
		}
	}
	



}
