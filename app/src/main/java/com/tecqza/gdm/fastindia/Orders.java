package com.tecqza.gdm.fastindia;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class Orders extends AppCompatActivity {

    SharedPrefs userSharedPrefs;
    Context context;
    Bundle bundle;
    ProcessDialog processDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        context=this;
        userSharedPrefs=new SharedPrefs(context, "USER");
        bundle=getIntent().getExtras();
        processDialog=new ProcessDialog(context,"PROCESSING");

        LoadOrders loadOrders=new LoadOrders();
        loadOrders.execute(userSharedPrefs.getSharedPrefs("id"));

    }


    class LoadOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject=new JSONObject(s);
                String status=jsonObject.getString("status");
                if(status.trim().equals("true")){
                    JSONArray orderArray=new JSONArray(jsonObject.getString("data"));
                    ArrayList<OrderItemsModel> order_list= OrderItemsModel.fromJson(orderArray);

                    RecyclerView patientRecyclerView = findViewById(R.id.order_recycler_view);
                    patientRecyclerView.clearOnScrollListeners();
                    patientRecyclerView.setNestedScrollingEnabled(true);
                    patientRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    patientRecyclerView.setAdapter(new OrderAdapter(order_list, context));
                }else{
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("customer_orders/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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


