package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StateModel {

    String id, name, code;
    public StateModel(JSONObject state) throws JSONException {
        this.id=state.getString("id");
        this.name=state.getString("name");
        this.code=state.getString("code");
    }

    public static ArrayList<StateModel> fromJson(JSONArray jsonObjects) {
        ArrayList<StateModel> states = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                states.add(new StateModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return states;
    }
}
