package net.teamshadowmc.nba.bungee;

import net.teamshadowmc.nba.json.JSONArray;
import net.teamshadowmc.nba.json.JSONException;
import net.teamshadowmc.nba.json.JSONObject;
import net.md_5.bungee.api.plugin.Plugin;

public class checkJson {

    public boolean isValidJSON(String jsonMsg) {
        return isValidObject(jsonMsg) || isValidArray(jsonMsg);
    }

    public boolean isValidObject(String jsonMsg) {

        try {
            new JSONObject(jsonMsg);
            return true;
        }

        catch (JSONException e) {
            return false;
        }
    }

    public boolean isValidArray(String jsonMsg) {

        try {
            new JSONArray(jsonMsg);
            return true;
        }

        catch (JSONException e) {
            return false;
        }
    }

}
