package net.teamshadowmc.novabungeeannouncer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;
import net.teamshadowmc.novabungeeannouncer.utils.NBAConfig;
import net.teamshadowmc.novabungeeannouncer.utils.checkJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerMessage {
	String message = "";
	ProxiedPlayer player = null;
	String permission = "";
	String type = "";
	checkJson json = new checkJson();
	
	public PlayerMessage(String msg, ProxiedPlayer pp, String perm, String type){
		message = msg;
		player = pp;
		permission = perm;
	}
	
	public PlayerMessage(NBAConfig.Announcement anc, ProxiedPlayer pp, String perm){
		message = anc.message;
		type = anc.type;
		player = pp;
		permission = perm;
	}

	public void sendMessage(){
		if(type.equals("text")){
			if(message.contains("&"))
				player.sendMessage(textBuilder(message));
			else{
				message = replaceValues(message);
				player.sendMessage(message);
			}
		}
		else if(type.equals("multitext")){
			for(String s : message.split("/n")){
				player.sendMessage(textBuilder(s));
			}
		}

		else if(type.equals("json")){
			message = replaceValues(message);
			if (json.isValidJSON(message)) {
				player.unsafe().sendPacket(new Chat(message));
			}
		}
		else if(type.equals("multijson")){
			message = replaceValues(message);
			if (json.isValidJSON(message)) {
				JSONArray jsa = new JSONArray(message);
				for (int i = 0; i < jsa.length(); i++) {
					player.unsafe().sendPacket(new Chat(((JSONObject) jsa.get(i)).toString()));
				}
			}
		}
		else if (type.equals("title")){
			Title t = NovaBungeeAnnouncer.instance.getProxy().createTitle();
			JSONObject jso = new JSONObject(message);
			if(jso.has("title"))
				t.title(new TextComponent(textBuilder(jso.getString("title"))));
			if(jso.has("subTitle"))
				t.subTitle(new TextComponent(textBuilder(jso.getString("subTitle"))));
			if(jso.has("stay"))
				t.stay(Integer.valueOf(jso.getString("stay")));
			if(jso.has("fadeIn"))
				t.fadeIn(Integer.valueOf(jso.getString("fadeIn")));
			if(jso.has("fadeOut"))
				t.fadeOut(Integer.valueOf(jso.getString("fadeOut")));
			t.send(player);
		}
		else if (type.equals("actionbar")){
			player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(textBuilder(message)));
		}
		else if (type.equals("jsonactionbar")){
			player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		}
	}
	
	public static void announceAnnouncement(NBAConfig.Announcement toSay, String serverName, ArrayList<String> servers, String permission){
		if(serverName.equals("global")||servers!=null&&servers.contains("global")){
			for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
				PlayerMessage pm = new PlayerMessage(toSay, p, permission);
				pm.checkSendMessage();
			}
		}
		else{
			try{
				if(servers!=null&&servers.size()>0){
					for(String s : servers){
						ServerInfo si = ProxyServer.getInstance().getServerInfo(s);
						if(si!=null)
							for(ProxiedPlayer p : si.getPlayers()){
								PlayerMessage pm = new PlayerMessage(toSay, p, permission);
								pm.checkSendMessage();
							}
					}
				}
				else{
					ServerInfo theS = ProxyServer.getInstance().getServerInfo(serverName);
					for(ProxiedPlayer p : theS.getPlayers()){
						PlayerMessage pm = new PlayerMessage(toSay, p, permission);
						pm.checkSendMessage();
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("Server " + serverName + " not found");

			}
		}
	}
	

	public static void sendAll( NBAConfig.Announcement message){
		Iterator<ProxiedPlayer> ppi = ProxyServer.getInstance().getPlayers().iterator();
		while(ppi.hasNext()){
			ProxiedPlayer pp = ppi.next();
			PlayerMessage pm = new PlayerMessage(message, pp, "");
			pm.checkSendMessage();
		}
	}

	public void checkSendMessage(){
		if(permission!=null && permission.length()>0){
			if(!NovaBungeeAnnouncer.perms.containsKey(player.getName())){
				NovaBungeeAnnouncer.perms.put(player.getName(), new ArrayList<String>());
				NovaBungeeAnnouncer.instance.getPerms(player.getName(), permission);
				NovaBungeeAnnouncer.queue.get(player.getName()).add(this);
			}
			else if(NovaBungeeAnnouncer.perms.get(player.getName()).contains("+"+permission)){
				sendMessage();
				return;
			}
			else if(NovaBungeeAnnouncer.perms.get(player.getName()).contains("-"+permission)){
				return;
			}
			else{
				NovaBungeeAnnouncer.instance.getPerms(player.getName(), permission);
				NovaBungeeAnnouncer.queue.get(player.getName()).add(this);
				return;
			}
		}
		else {
			sendMessage();
			return;
		}
	}
	
	public String textBuilder(String input){
		input = replaceValues(input);
		String builder = "";
		for(int i = 0; i < input.length(); i++){
			if(input.charAt(i)!='&')
				builder+=input.charAt(i);
			else
				break;
		}
		boolean first = true;
		for(String g : input.split("&")){
			if(!first){
				String chatColor = null;
				for(String s : g.split("(?!^)")){
					if(chatColor==null){
						chatColor = s;
					}
					else
						builder+=ChatColor.COLOR_CHAR+chatColor+s;
				}
			}
			first = false;
		}
		return builder;
	}
	
	public String replaceValues(String input){
		input = input.replaceAll("%playername%", player.getName());
		return input;
	}
	
	public static void sendEvent(String eventName, String arg){
		if(NovaBungeeAnnouncer.config.nonannouncements.containsKey(eventName)){
			NBAConfig.BroadcastMap bm = NovaBungeeAnnouncer.config.nonannouncements.get(eventName);
			NBAConfig.Announcement an = bm.announcement.clone();
			an.message = an.message.replaceAll("<<1>>", arg);
			PlayerMessage.announceAnnouncement(an, "", bm.servers, bm.permission);
		}
	}
}
