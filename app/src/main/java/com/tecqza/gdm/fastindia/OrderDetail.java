package com.tecqza.gdm.fastindia;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OrderDetail extends AppCompatActivity {

    SharedPrefs userSharedPrefs;
    Context context;
    ProcessDialog processDialog;
    Bundle bundle;
    TextView tv_total, gst, tv_status, tot_gst;
    TextView vendor_name, vendor_phone_pe, vendor_paytm, vendor_payment_status, text, text2;
    LinearLayout delivery_boy_container;
    String delivery_boy_no, vendor_no;
    Button call_delivery_boy, call_vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        context = this;
        userSharedPrefs = new SharedPrefs(context, "USER");
        processDialog = new ProcessDialog(context, "PROCESSING");
        bundle = getIntent().getExtras();

        tv_total = findViewById(R.id.total);
        delivery_boy_no = "";
        vendor_no = "";

        call_delivery_boy = findViewById(R.id.call_delivery_boy);
        call_vendor = findViewById(R.id.call_vendor);
        tv_status = findViewById(R.id.status);
        tot_gst = findViewById(R.id.tot_gst);
        gst = findViewById(R.id.gst);

        vendor_name = findViewById(R.id.vendor_name);
        vendor_phone_pe = findViewById(R.id.vendor_phone_pe);
        vendor_paytm = findViewById(R.id.vendor_paytm);
        vendor_payment_status = findViewById(R.id.vendor_payment_status);
        text = findViewById(R.id.text);
        text2 = findViewById(R.id.text2);

        call_delivery_boy.setOnClickListener(view -> {
            Uri u = Uri.parse("tel:" + delivery_boy_no);
            Intent i = new Intent(Intent.ACTION_DIAL, u);
            startActivity(i);
        });
        call_vendor.setOnClickListener(view -> {
            Uri u = Uri.parse("tel:" + vendor_no);
            Intent i = new Intent(Intent.ACTION_DIAL, u);
            startActivity(i);
        });

        LoadOrderDetails loadOrderDetails = new LoadOrderDetails();
        loadOrderDetails.execute(bundle.getString("order_id"));


    }



    class LoadOrderDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                if (status.trim().equals("true")) {
                    JSONObject order_detail = new JSONObject(jsonObject.getString("order"));
                    tv_status.setText(order_detail.getString("status"));
                    vendor_name.setText(order_detail.getString("vendor_name"));
                    if (order_detail.getString("delivery_boy_mobile").equals("null")) {
                        call_delivery_boy.setVisibility(View.GONE);
                    } else {
                        delivery_boy_no = order_detail.getString("delivery_boy_mobile");
                    }
                    vendor_no = order_detail.getString("vendor_mobile");


                    if (order_detail.getString("payment_status").equals(""))
                        vendor_payment_status.setText("PENDING");
                    else
                        vendor_payment_status.setText(order_detail.getString("payment_status"));


                    if (order_detail.getString("phone_pe").equals("")) {
                        vendor_phone_pe.setVisibility(View.GONE);
                        text.setVisibility(View.GONE);
                    } else
                        vendor_phone_pe.setText(order_detail.getString("phone_pe"));

                    if (order_detail.getString("paytm").equals("")) {
                        vendor_paytm.setVisibility(View.GONE);
                        text2.setVisibility(View.GONE);
                    } else
                        vendor_paytm.setText(order_detail.getString("paytm"));


                    JSONArray ordered_items_jsonArray = new JSONArray(jsonObject.getString("ordered_items"));
                    ArrayList<OrderedItemModel> items_list = OrderedItemModel.fromJson(ordered_items_jsonArray);

                    float total = 0f;
                    for (OrderedItemModel i : items_list) {
                        total = total + Float.parseFloat(i.amount);
                    }
                    String Total = order_detail.getString("total_amount");
                    String gsst = order_detail.getString("gst");
                    String tot_gsts = String.valueOf(Float.parseFloat(Total) + Float.parseFloat(gsst));
                    tv_total.setText(Total);
                    gst.setText(gsst);
                    tot_gst.setText(tot_gsts);

                    RecyclerView recyclerView = findViewById(R.id.ordered_item_recyclerview);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new OrderedItemsAdapter(items_list, context, order_detail.getString("status")));

                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {

            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {

            String urls = context.getString(R.string.base_url).concat("order_details/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("order_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

                bufferedWriter.write(post_Data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String result = "", line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return e.toString();
            }

        }
    }


}

