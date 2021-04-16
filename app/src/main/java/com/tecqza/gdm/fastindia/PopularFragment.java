package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PopularFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView text_address;

    TextView no_service;


    public PopularFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.popular_fragment, container, false);

        ImageView back = view.findViewById(R.id.back);
        text_address = view.findViewById(R.id.text_address);
        ImageView chat = view.findViewById(R.id.chat);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        no_service = view.findViewById(R.id.no_service);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });


        text_address.setText(userSharedPrefs.getSharedPrefs("address"));

        LoadPopularVendors loadPopularVendors = new LoadPopularVendors();
        loadPopularVendors.execute(userSharedPrefs.getSharedPrefs("city_id"),userSharedPrefs.getSharedPrefs("id"));
        return view;
    }

    private void remove() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    class LoadPopularVendors extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("true")) {
                    JSONArray vendorJsonArray = new JSONArray(jsonObject.getString("data"));
                    ArrayList<VendorModel> vendor_list = VendorModel.fromJson(vendorJsonArray);

                    RecyclerView recyclerView = view.findViewById(R.id.vendorRecyclerView);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new VendorAdapter(vendor_list, context));
                    if (vendor_list.size() == 0) {
                        no_service.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
//                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("popular_vendors/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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
