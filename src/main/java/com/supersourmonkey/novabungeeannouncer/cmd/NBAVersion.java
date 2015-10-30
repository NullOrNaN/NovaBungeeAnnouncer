package com.supersourmonkey.novabungeeannouncer.cmd;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.supersourmonkey.novabungeeannouncer.NovaBungeeAnnouncer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Logger;

public class NBAVersion extends Command {

    String currentVer;
    String pName;

    String jenkinsData = "http://ci.teamshadowmc.net/job/NovaBungeeAnnouncer/lastSuccessfulBuild/git/api/json?pretty=true&tree=lastBuiltRevision[SHA1]";
    String jenkinsDL = "http://ci.teamshadowmc.net/job/NovaBungeeAnnouncer/lastSuccessfulBuild/";

    String gitHubData = "https://raw.githubusercontent.com/MatthewM/NovaBungeeAnnouncer/master/nba.json";
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

    /* GitHub Repo or Gist-based */
    public class VerInfo {
        private Map<String, VerData> result;
    }
    public class VerData {
        private String version;
        private String download;
    }

    /* Jenkins Build Info */
    public class JenkinsVerInfo {
        JenkinsVerData lastBuiltRevision;
    }
    public class JenkinsVerData {
        private String SHA1;
    }

    @Override
    public void execute(CommandSender sender, String[] string) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (currentVer.contains("-git")) {
                checkJenkins();
            }
            else {
                checkGH();
            }
        }
    }

    public void checkJenkins() {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(jenkinsData);
                    Gson gson = new Gson();
                    URLConnection conn = url.openConnection();
                    JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                    JenkinsVerInfo jenkinsInfo = gson.fromJson(reader,JenkinsVerInfo.class);

                    String latestGit = jenkinsInfo.lastBuiltRevision.SHA1;
                    String  currentGit = currentVer.substring(currentVer.indexOf("-git") + 5);

                    //logger.info(String.format("L: %s | C: %s", latestGit, currentGit));

                    if (currentGit.equals(latestGit)) {
                        outResult(false, true, latestGit, jenkinsDL);
                    } else {
                        outResult(true, true, latestGit, jenkinsDL);
                    }
                }
                catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void checkGH() {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {

                try {
                    String branch = "stable";

                    URL url = new URL(gitHubData);
                    Gson gson = new Gson();
                    URLConnection conn = url.openConnection();
                    JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                    VerInfo verinfo = gson.fromJson(reader, VerInfo.class);
                    String latestVer = verinfo.result.get(branch).version;
                    String latestDL = verinfo.result.get(branch).download;
                    if (currentVer.equals(latestVer)) {
                        outResult(false, false, latestVer, latestDL);
                    } else {
                        outResult(true, false, latestVer, latestDL);
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void outResult(Boolean hasUpdate, Boolean devel, String latest, String download) {
        logger.info(String.format("[%s] Installed version is %s. Checking for update..", pName, currentVer));

        if (hasUpdate) {
            if (devel) {
                logger.info(String.format("[%s] Update available! Latest is based off of git commit %s.", pName, latest));
            }
            else {
                logger.info(String.format("[%s] Update available! Latest version is %s.", pName, latest));
            }
            logger.info(String.format("[%s] Download at %s", pName, download));
        }
        else {
            logger.info(String.format("[%s] No updates found!", pName));
        }
    }
}
