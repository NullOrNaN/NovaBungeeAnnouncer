package net.teamshadowmc.novabungeeannouncer;

import net.teamshadowmc.novabungeeannouncer.utils.NBAConfig;

public class AnnounceMessage implements Runnable{
	public NBAConfig.MessageMap server;
	public String serverName;
	public int nextAnnounce = 0;

	public AnnounceMessage(NBAConfig.MessageMap server, String serverName){
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
			NBAConfig.Announcement toSay = server.announcements.get(nextAnnounce);
			PlayerMessage.announceAnnouncement(toSay, serverName, server.servers, server.permission);
			nextAnnounce++;
		}
	}
	



}
