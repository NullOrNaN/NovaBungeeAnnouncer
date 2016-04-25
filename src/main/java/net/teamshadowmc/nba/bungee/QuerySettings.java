package net.teamshadowmc.nba.bungee;

import java.io.File;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.md_5.bungee.api.plugin.Plugin;

public class QuerySettings extends Config {
	public QuerySettings(Plugin plugin) {
		CONFIG_HEADER = new String[]{"NovaBungeeAnnouncer query config file"};
		CONFIG_FILE = new File(plugin.getDataFolder(), "NovaBungeeQuery.yml");
	}
	
	@Comment("Server to check for economy data")
	public String econserver = "lobby";
}
