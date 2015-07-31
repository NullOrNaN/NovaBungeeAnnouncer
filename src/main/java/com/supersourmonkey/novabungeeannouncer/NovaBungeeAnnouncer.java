package com.supersourmonkey.novabungeeannouncer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import net.cubespace.Yamler.Config.ConfigSection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.supersourmonkey.novabungeeannouncer.AnnouncerConfig.Announcement;
import com.supersourmonkey.novabungeeannouncer.AnnouncerConfig.MessageMap;
import com.supersourmonkey.novabungeeannouncer.AnnouncerConfig.BroadcastMap;

public class NovaBungeeAnnouncer extends Plugin implements Listener {
	public static AnnouncerConfig config;
	public static NovaBungeeAnnouncer instance;
	ArrayList<ScheduledTask> tasks = new ArrayList<ScheduledTask>();
	public static HashMap<String, ArrayList<String>> perms = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<PlayerMessage>> queue = new HashMap<String, ArrayList<PlayerMessage>>();

	public static HashMap<String, PlayerMessage> queBroadcast = new HashMap<String, PlayerMessage>();

	@Override
	public void onEnable() {
		instance = this;
		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new NovaFindCommand("nbareload",this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new NBASend("nbasend",this));
		ProxyServer.getInstance().registerChannel("NBA");
		config = new AnnouncerConfig(this);
		try {
			config.init();
			config.save();
			load();
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public void onDisable(){
		//getProxy().getScheduler().cancel(this);
		//for(ScheduledTask task : tasks){
		//	getProxy().getScheduler().cancel(task);
		//	//task.cancel();
		//}
		//tasks.clear();
	}

	@EventHandler
	public void onMessageRecieve(PluginMessageEvent event) {

		if(event.getTag().equals("NBA")){
			ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
			String playerName = in.readUTF();
			String permissionValue = in.readUTF();
			if(!perms.containsKey(playerName)){
				perms.put(playerName, new ArrayList<String>());
			}
			perms.get(playerName).add(permissionValue);
			if(permissionValue.startsWith("+")){
				if(queue.containsKey(playerName)){
					for(int i = 0; i < queue.get(playerName).size(); i++){
						PlayerMessage qm = queue.get(playerName).get(i);
						if(qm.permission.equals(permissionValue.substring(1))){
							qm.sendMessage();
							queue.remove(qm);
							break;
						}
					}
				}
				if(queBroadcast.containsKey(playerName)){
					queBroadcast.get(playerName).sendMessage();
					queBroadcast.clear();
				}
			}

		}
	}

	@EventHandler
	public void onPlayerJoin(final PostLoginEvent event){
		ArrayList<PlayerMessage> qms = new ArrayList<PlayerMessage>();
		queue.put(event.getPlayer().getName(), qms);
		getPerms(event.getPlayer().getName(), "nba.send");
		PlayerMessage.sendEvent("onLogin", event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerLeave(final PlayerDisconnectEvent event){
		PlayerMessage.sendEvent("onDisconnect", event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerKicked(final ServerKickEvent event){
		PlayerMessage.sendEvent("onKick", event.getPlayer().getName());
	}


	public void getPerms(String player, String permMessage){
		if(config.permissionServer!=null&&config.permissionServer.length()>0){
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(player);
			out.writeUTF(permMessage);
			ProxyServer.getInstance().getServerInfo(config.permissionServer).sendData("NBA", out.toByteArray());
		}
	}


	public void load(){
		try {
			config.load();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(ScheduledTask task : tasks){
			getProxy().getScheduler().cancel(this);
			task.cancel();
			task = null;
		}
		tasks.clear();
		queue.clear();
		perms.clear();
		Iterator<ProxiedPlayer> ppi = ProxyServer.getInstance().getPlayers().iterator();
		while(ppi.hasNext()){
			ProxiedPlayer pp = ppi.next();
			ArrayList<PlayerMessage> qms = new ArrayList<PlayerMessage>();
			queue.put(pp.getName(), qms);
		}
		System.out.println("Length of servers: " + config.servers.size());
		for(Entry<String, MessageMap> s : config.servers.entrySet()){
			String serverName = s.getKey();
			System.out.println(s.getValue().getClass());
			System.out.println(s.getValue().getRawMap().toString());
			System.out.println(s.getValue().get("message"));
			MessageMap serverConfig = s.getValue();
			ScheduledTask task = getProxy().getScheduler().schedule(this, new AnnounceMessage(serverConfig, serverName), serverConfig.offset, serverConfig.delay, TimeUnit.SECONDS);
			System.out.println("New task scheduled with offset " + serverConfig.offset + " and delay " + serverConfig.delay);
			tasks.add(task);
			if(config.order.equals("random"))
				Collections.shuffle(serverConfig.announcements);
		}
		if(config.servers.size()==0){
			MessageMap example = new MessageMap();
			example.offset = 0;
			example.delay = 60;
			Announcement a = new Announcement();
			a.message = "Just a simple text announcement message";
			a.type = "text";
			Announcement b = new Announcement();
			b.message = "{\"text\":\"A simple json message\",\"color\":\"gold\"}";
			b.type = "json";
			example.announcements.add(a);
			example.announcements.add(b);
			config.servers.put("global", example);			
		}
		if(config.nonannouncements.size()==0){
			BroadcastMap bm = new BroadcastMap();
			bm.announcement = new Announcement();
			bm.announcement.message = "Hello, <user>";
			bm.permission = "super.op";
			bm.announcement.type="text";
			bm.servers = new ArrayList<String>();
			bm.servers.add("global");
			config.nonannouncements.put("demo", bm);
		}
		if(config.permissionCacheTime!=0){
			getProxy().getScheduler().schedule(this, new Runnable() {
				@Override
				public void run() {

					perms.clear();
					for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()){
						getPerms(pp.getName(), "nba.send");
					}
				}
			}, 0, config.permissionCacheTime, TimeUnit.MINUTES);
		}

		try {
			config.save();
		} catch (Exception e) {
			System.out.println("Error saving default config values");
			e.printStackTrace();
		}
	}
}
