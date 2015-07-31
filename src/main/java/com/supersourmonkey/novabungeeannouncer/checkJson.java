package com.supersourmonkey.novabungeeannouncer;

import com.supersour.json.JSONArray;
import com.supersour.json.JSONException;
import com.supersour.json.JSONObject;
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
