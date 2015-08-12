package net.teamshadowmc.novabungeebukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;

public class NovaBungeeAnnouncer extends JavaPlugin implements PluginMessageListener, Listener {
	public static Permission permission = null;

	@Override
	public void onEnable() {
	    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "NBA");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "NBA", this);
	    setupPermissions();
	}
	
	public void onDisable() {
	}
	

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		try{
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    String value = in.readUTF();
	    //player name = subchannel
	    //requested perm = utf 2
	    sendPerms(subchannel, value);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void sendPerms(String playerN, String permissionS){
		  ByteArrayDataOutput out = ByteStreams.newDataOutput();
		  out.writeUTF(playerN);
		  if(permission.has("world", playerN, permissionS)){
			  out.writeUTF("+"+permissionS);
		  }
		  else{
			  out.writeUTF("-"+permissionS);
		  }

		  // If you don't care about the player
		  Player player = new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0);
		  // Else, specify them
		  //Player player = Bukkit.getPlayerExact("Example");

		  player.sendPluginMessage(this, "NBA", out.toByteArray());
	}
	private boolean setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
}
