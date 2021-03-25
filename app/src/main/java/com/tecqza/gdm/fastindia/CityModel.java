package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CityModel {

    String id, name, state_id, code;
    public CityModel(JSONObject city) throws JSONException {
        this.id=city.getString("id");
        this.name=city.getString("name");
        this.state_id=city.getString("state_id");
        this.code=city.getString("code");
    }

    public static ArrayList<CityModel> fromJson(JSONArray jsonObjects) {
        ArrayList<CityModel> cities = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                cities.add(new CityModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cities;
    }
}
