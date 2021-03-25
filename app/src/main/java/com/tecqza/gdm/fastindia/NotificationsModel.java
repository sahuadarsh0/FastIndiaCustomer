package com.tecqza.gdm.fastindia;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class NotificationsModel {

    String id, title, message;

    public NotificationsModel(JSONObject item) throws JSONException {
        this.id = item.getString("id");
        this.title = item.getString("title");
        this.message = item.getString("notification");
    }

    public static ArrayList<NotificationsModel> fromJson(JSONArray jsonObjects) {
        ArrayList<NotificationsModel> items = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                items.add(new NotificationsModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return items;
    }
}
