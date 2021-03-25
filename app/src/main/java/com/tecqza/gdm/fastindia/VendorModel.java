package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VendorModel {

    String id, name, email, mobile, business_name, business_contact, address, payment_mobile_no, token, open, image, favourite_vendor_id;

    public VendorModel(JSONObject vendor) throws JSONException {
        this.id = vendor.getString("id");
        this.name = vendor.getString("name");
        this.email = vendor.getString("email");
        this.mobile = vendor.getString("mobile");
        this.business_name = vendor.getString("business_name");
        this.business_contact = vendor.getString("business_contact");
        this.address = vendor.getString("address");
        this.payment_mobile_no = vendor.getString("payment_mobile_no");
        this.token = vendor.getString("token");
        this.open = vendor.getString("open");
        this.favourite_vendor_id = vendor.getString("favourite_vendor_id");
        this.image = vendor.getString("image");
    }

    public static ArrayList<VendorModel> fromJson(JSONArray jsonObjects) {
        ArrayList<VendorModel> vendors = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                vendors.add(new VendorModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vendors;
    }
}
