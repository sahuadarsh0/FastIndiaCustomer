package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductModel {

    String vpid, id, name, image, qty, unit, typ, product_description, amount, flag, cdt, pamount, cqty;

    DatabaseHelper myDb;

    public ProductModel(JSONObject product, Context context) throws JSONException {
        this.vpid=product.getString("vpid");
        this.id=product.getString("id");
        this.name=product.getString("name");
        this.image=product.getString("image");
        this.qty=product.getString("qty");
        this.unit=product.getString("unit");
        this.typ=product.getString("type");
        this.product_description=product.getString("product_description");
        this.amount=product.getString("amount");
        this.flag=product.getString("flag");
        this.cdt=product.getString("cdt");
        this.pamount=product.getString("pamount");

        myDb = new DatabaseHelper(context);

        Cursor res=myDb.getItemOfVPId(product.getString("vpid"));

        if(res.getCount()>0){
            while (res.moveToNext()){
                this.cqty=res.getString(3);
            }
        }else{
            this.cqty="0";
        }


    }

    public static ArrayList<ProductModel> fromJson(JSONArray jsonObjects, Context context) {
        ArrayList<ProductModel> products = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                products.add(new ProductModel(jsonObjects.getJSONObject(i), context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return products;
    }
}
