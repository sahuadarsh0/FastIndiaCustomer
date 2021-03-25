package com.tecqza.gdm.fastindia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderItemsModel {

    String id, customer_id, delivery_boy_id, vendor_id, order_type, state_id, city_id, address, landmark, amount, extra_amount, total_amount, gst, status, payment_status, payment_mode, payment_id, flag, cdt, customer_name, customer_address, customer_landmark, customer_mobile, vendor_name, vendor_mobile, paytm, phone_pe, vendor_image, vendor_address, delivery_boy_name, delivery_boy_mobile, order_type_name;

    Date cDT;

    public OrderItemsModel(JSONObject order) throws JSONException, ParseException {
        this.id = order.getString("id");
        this.customer_id = order.getString("customer_id");
        this.delivery_boy_id = order.getString("delivery_boy_id");
        this.vendor_id = order.getString("vendor_id");
        this.order_type = order.getString("order_type");
        this.state_id = order.getString("state_id");
        this.city_id = order.getString("city_id");
        this.address = order.getString("address");
        this.landmark = order.getString("landmark");
        this.amount = order.getString("amount");
        this.extra_amount = order.getString("extra_amount");
        this.total_amount = order.getString("total_amount");
        this.gst = order.getString("gst");
        this.status = order.getString("status");
        this.payment_status = order.getString("payment_status");
        this.payment_mode = order.getString("payment_mode");
        this.payment_id = order.getString("payment_id");
        this.flag = order.getString("flag");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.cDT = format.parse(order.getString("cdt"));

        this.cdt = order.getString("cdt");
        this.customer_name = order.getString("customer_name");
        this.customer_address = order.getString("customer_address");
        this.customer_landmark = order.getString("customer_landmark");
        this.customer_mobile = order.getString("customer_mobile");
        this.vendor_name = order.getString("vendor_name");
        this.vendor_mobile = order.getString("vendor_mobile");
        this.paytm = order.getString("paytm");
        this.phone_pe = order.getString("phone_pe");
        this.vendor_image = order.getString("vendor_image");
        this.vendor_address = order.getString("vendor_address");
        this.delivery_boy_name = order.getString("delivery_boy_name");
        this.delivery_boy_mobile = order.getString("delivery_boy_mobile");
        this.order_type_name = order.getString("order_type_name");
    }

    public static ArrayList<OrderItemsModel> fromJson(JSONArray jsonObjects) {
        ArrayList<OrderItemsModel> orders = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                orders.add(new OrderItemsModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }
}


