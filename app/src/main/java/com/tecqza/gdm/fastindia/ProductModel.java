package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductModel {

    String vpid, id, name, image, type, product_description,  flag, cdt, in_stock, cqty, varieties;

    DatabaseHelper myDb;

    public ProductModel(JSONObject product, Context context) throws JSONException {
        this.vpid=product.getString("vpid");
        this.id=product.getString("id");
        this.name=product.getString("name");
        this.image=product.getString("image");
        this.type=product.getString("type");
        this.in_stock=product.getString("in_stock");
        this.product_description=product.getString("product_description");
        this.flag=product.getString("flag");
        this.cdt=product.getString("cdt");
        this.varieties=product.getString("varieties");

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
