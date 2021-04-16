package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VarietiesModel {

    String vpid, id, qty, mrp, selling_price, flag, cdt;

    public VarietiesModel(JSONObject product) throws JSONException {
        this.vpid = product.getString("vpid");
        this.id = product.getString("id");
        this.qty = product.getString("qty");
        this.mrp = product.getString("mrp");
        this.selling_price = product.getString("selling_price");
        this.flag = product.getString("flag");
        this.cdt = product.getString("cdt");

    }

    public static ArrayList<VarietiesModel> fromJson(JSONArray jsonObjects) {
        ArrayList<VarietiesModel> varieties = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                varieties.add(new VarietiesModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return varieties;
    }
}
