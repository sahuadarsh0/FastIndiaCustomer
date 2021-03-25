package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartItemsModel {

    String id, vpid, name, qty, extra, vendor_id, extra_qty, image, amount;

    public CartItemsModel(JSONObject item) throws JSONException {
        this.id = item.getString("id");
        this.vpid = item.getString("vpid");
        this.name = item.getString("name");
        this.qty = item.getString("qty");
        this.extra = item.getString("extra");
        this.vendor_id = item.getString("vendor_id");
        this.extra_qty = item.getString("extra_qty");
        this.image = item.getString("image");
        this.amount = item.getString("amount");
    }

    public static ArrayList<CartItemsModel> fromJson(JSONArray jsonObjects) {
        ArrayList<CartItemsModel> items = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                items.add(new CartItemsModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}
