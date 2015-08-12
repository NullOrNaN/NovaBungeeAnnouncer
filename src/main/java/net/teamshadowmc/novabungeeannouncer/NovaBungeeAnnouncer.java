package net.teamshadowmc.novabungeeannouncer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class NovaBungeeAnnouncer extends Plugin implements Listener {
	//public static AnnouncerConfig config;
	public static NBAConfig config;

	public static NovaBungeeAnnouncer instance;

	ArrayList<ScheduledTask> tasks = new ArrayList<ScheduledTask>();
	public static HashMap<String, ArrayList<String>> perms = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, ArrayList<PlayerMessage>> queue = new HashMap<String, ArrayList<PlayerMessage>>();

	public static HashMap<String, PlayerMessage> queBroadcast = new HashMap<String, PlayerMessage>();

	private boolean debug = true;

	private Utils util;

	@Override
	public void onEnable() {
		instance = this;
		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new NovaFindCommand("nbareload", this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new NBASend("nbasend", this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new NBASet("nbaset", this));
		ProxyServer.getInstance().registerChannel("NBA");

		config = new NBAConfig(this);
		util = new Utils(this);
		load();

	}
	@Override
	public void onDisable() {
		for (ScheduledTask task : tasks) {
			getProxy().getScheduler().cancel(this);
			task.cancel();
			task = null;
		}

		tasks.clear();
		queue.clear();
		perms.clear();

		getProxy().getPluginManager().unregisterCommands(this);

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

	public void load() {
		if (debug) {
			util.log("[DEBUG] Loading our config");
			util.log("[DEBUG] This will show up on plugin startup and if you have debug.enable set to true");
		}
		try {
			config.loadCfg();
			if (debug) util.log("[DEBUG] Loaded config.yml. Starting plugin routines!");
			start();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	public void start() {

		for (ScheduledTask task : tasks) {
			getProxy().getScheduler().cancel(this);
			task.cancel();
			task = null;
		}

		tasks.clear();
		queue.clear();
		perms.clear();

		checkJson checkJson = new checkJson();

		Iterator<ProxiedPlayer> ppi = ProxyServer.getInstance().getPlayers().iterator();
		while (ppi.hasNext()) {
			ProxiedPlayer pp = ppi.next();
			ArrayList<PlayerMessage> qms = new ArrayList<PlayerMessage>();
			queue.put(pp.getName(), qms);
		}


		for (Entry<String, NBAConfig.MessageMap> s : config.servers.entrySet()) {
			String serverName = s.getKey();
			NBAConfig.MessageMap serverConfig = s.getValue();

			ScheduledTask task = getProxy().getScheduler().schedule(this, new AnnounceMessage(serverConfig, serverName), serverConfig.offset, serverConfig.delay, TimeUnit.SECONDS);
			System.out.println("New task scheduled with offset " + serverConfig.offset + " and delay " + serverConfig.delay);
			tasks.add(task);
			/*
			if (config.getConfigString("order").equalsIgnoreCase("random")) {
				Collections.shuffle(serverConfig.announcements);
			}
			*/
		}

		if (config.servers.size() == 0 || config.nonannouncements.size() == 0) {
			getLogger().severe("The formatting seems to be all wrong!");
		}
		else for (Entry<String, NBAConfig.MessageMap> s : config.servers.entrySet()) {
			String serverName = s.getKey();
			NBAConfig.MessageMap serverConfig = s.getValue();

			for (int msgCount = 0; msgCount < serverConfig.announcements.size(); msgCount = msgCount + 1) {
				String msgType = serverConfig.announcements.get(msgCount).type;
				String msgMsg = serverConfig.announcements.get(msgCount).message;

				if (msgType.equalsIgnoreCase("json") || msgType.equalsIgnoreCase("multijson")) {
					if (!(checkJson.isValidJSON(msgMsg))) {
						getLogger().warning(String.format("Malformed JSON formatting found in servers.%s in message #%d", serverName, (msgCount + 1)));
					}
				}
			}
		}
		for (Entry<String, NBAConfig.BroadcastMap> s : config.nonannouncements.entrySet()) {
			String serverName = s.getKey();
			NBAConfig.BroadcastMap serverConfig = s.getValue();
			String ancType = serverConfig.announcement.type;
			String ancMsg = serverConfig.announcement.message;

			if (ancType.equalsIgnoreCase("json") || ancType.equalsIgnoreCase("multijson")) {

				if (!(checkJson.isValidJSON(ancMsg))) { //Oh no! Looks like the user has used an invalid JSON string!
					getLogger().warning(String.format("Malformed JSON formatting found in servers.%s.message!", serverName));
				}
			}
		}
		if (config.permissionCacheTime!=0){
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
	}

}
