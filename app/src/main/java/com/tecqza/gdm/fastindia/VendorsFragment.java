package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.ArrayList;

public class VendorsFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView text_address;
    VendorAdapter vendorAdapter;
    EditText searchVendor;
    ArrayList<VendorModel> vendor_list;

    TextView no_service;

    public VendorsFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.vendors_fragment, container, false);

        text_address = view.findViewById(R.id.text_address);
        ImageView banner = view.findViewById(R.id.banner);
        TextView banner_text = view.findViewById(R.id.banner_text);
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        no_service = view.findViewById(R.id.no_service);

        searchVendor = view.findViewById(R.id.searchVendor);

        searchVendor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<VendorModel> newList = new ArrayList<>();
                for (VendorModel vendor : vendor_list) {
                    if (vendor.name.toLowerCase().contains(searchVendor.getText().toString())) {
                        newList.add(vendor);
                    }
                }
                vendorAdapter.updateList(newList);
            }
        });

        text_address.setText(userSharedPrefs.getSharedPrefs("address"));
        String order_type = userSharedPrefs.getSharedPrefs("order_type");

        switch (order_type) {
            case "101":
                banner.setImageResource(R.drawable.grocery_banner);
                banner_text.setText("Groceries");
                break;
            case "102":
                banner.setImageResource(R.drawable.medical_banner);
                banner_text.setText("Medical");
                break;
            case "103":
                banner.setImageResource(R.drawable.dairy_banner);
                banner_text.setText("Dairy");
                break;
        }
        LoadVendors loadVendors = new LoadVendors();
        loadVendors.execute(userSharedPrefs.getSharedPrefs("city_id"), userSharedPrefs.getSharedPrefs("order_type"), userSharedPrefs.getSharedPrefs("id"));
        return view;
    }


    class LoadVendors extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            super.onPostExecute(s);
            processDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("true")) {
                    JSONArray vendorJsonArray = new JSONArray(jsonObject.getString("data"));
                    vendor_list = VendorModel.fromJson(vendorJsonArray);

                    RecyclerView recyclerView = view.findViewById(R.id.vendorRecyclerView);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    vendorAdapter = new VendorAdapter(vendor_list, context);
                    recyclerView.setAdapter(vendorAdapter);

                    if (vendor_list.size() == 0) {
                        no_service.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("vendors/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");

                bufferedWriter.write(post_Data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
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
