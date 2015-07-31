package net.teamshadowmc.novabungeeannouncer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
