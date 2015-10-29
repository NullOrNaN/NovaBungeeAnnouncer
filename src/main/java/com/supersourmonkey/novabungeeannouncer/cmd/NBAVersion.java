package com.supersourmonkey.novabungeeannouncer.cmd;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.supersourmonkey.novabungeeannouncer.NovaBungeeAnnouncer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Logger;

public class NBAVersion extends Command {

    String currentVer;
    String pName;
    String branch = "stable";
    String uri = "https://raw.githubusercontent.com/MatthewM/NovaBungeeAnnouncer/master/nba.json";
    String downloadURL = "https://www.spigotmc.org/resources/novabungeeannouncer.788";

    NovaBungeeAnnouncer plugin;
    Logger logger = ProxyServer.getInstance().getLogger();

    public NBAVersion(String name, NovaBungeeAnnouncer p)
    {
        super(name);
        plugin = p;
        currentVer = this.plugin.getDescription().getVersion();
        pName = this.plugin.getDescription().getName();
    }

    public class VerInfo {
        public Map<String, VerData> branch;
    }
    public class VerData {
        private String version;
        private String download;
    }
    @Override
    public void execute(CommandSender sender, String[] string) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(uri);
                        Gson gson = new Gson();
                        URLConnection conn = url.openConnection();
                        JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                        VerInfo verinfo = gson.fromJson(reader, VerInfo.class);
                        String latestVer = verinfo.branch.get(branch).version;
                        String latestDL = verinfo.branch.get(branch).download;
                        if (currentVer.equals(latestVer)) {
                            logger.info(String.format("[%s] is up to date!", pName));
                        } else {
                            logger.info(String.format("[%s] Update available! Latest version is %s.", pName, latestVer));
                            logger.info(String.format("[%s] Download at %s", pName, latestDL));
                        }
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }
    }
}
