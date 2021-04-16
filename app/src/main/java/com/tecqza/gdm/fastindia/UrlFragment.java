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

public class UrlFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView text_address,title,data;
    String url;

    public UrlFragment(Context context, String url) {
        this.context = context;
        this.url = url;
        processDialog = new ProcessDialog(context, "Loading..");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.url_fragment, container, false);


        ImageView back = view.findViewById(R.id.back);
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });



        text_address = view.findViewById(R.id.text_address);
        title = view.findViewById(R.id.title);
        data = view.findViewById(R.id.data);


        text_address.setText(userSharedPrefs.getSharedPrefs("address"));


        LoadUrl loadUrl = new LoadUrl();
        loadUrl.execute();

        return view;

    }


    class LoadUrl extends AsyncTask<String, Void, String> {

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
                title.setText(jsonObject.getString("title"));
                data.setText(jsonObject.getString("data"));

            } catch (JSONException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.file_base_url).concat("json/").concat(url);
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
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
