package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderedItemModel {

    public String id, order_id, product, image, qty, amount, cdt;

    public OrderedItemModel(JSONObject item) throws JSONException {
        this.id=item.getString("id");
        this.order_id=item.getString("order_id");
        this.product=item.getString("product");
        this.image=item.getString("image");
        this.qty=item.getString("qty");
        this.amount=item.getString("amount");
        this.cdt=item.getString("cdt");
    }

    public static ArrayList<OrderedItemModel> fromJson(JSONArray jsonObjects) {
        ArrayList<OrderedItemModel> items = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                items.add(new OrderedItemModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}
