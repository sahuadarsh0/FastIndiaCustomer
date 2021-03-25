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

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.HashMap;

public class OrderTypeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    BottomNavigationView bottomNavigationView;
    View view;
    ImageView grocery, medicine, dairy;
    TextView view_all;
    private SliderLayout mDemoSlider;

    TextView text_address;
    RequestOptions requestOptions;
    HashMap<String, String> url_maps = new HashMap<>();

    public OrderTypeFragment(Context context, BottomNavigationView bottomNavigationView) {
        this.context = context;
        this.processDialog = new ProcessDialog(context, "");
        this.userSharePrefs = new SharedPrefs(context, "USER");
        this.bottomNavigationView = bottomNavigationView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.order_type_fragment, container, false);

        LoadPopularVendors loadPopularVendors = new LoadPopularVendors();
        loadPopularVendors.execute(userSharePrefs.getSharedPrefs("city_id"), userSharePrefs.getSharedPrefs("id"));
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });

        if (userSharePrefs.getSharedPrefs("city_id") == null)
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment(context, bottomNavigationView)).addToBackStack(null).commit();


        text_address = view.findViewById(R.id.text_address);
        mDemoSlider = view.findViewById(R.id.slider);
        grocery = view.findViewById(R.id.grocery);
        medicine = view.findViewById(R.id.medicine);
        dairy = view.findViewById(R.id.dairy);
        view_all = view.findViewById(R.id.view_all);

        text_address.setText(userSharePrefs.getSharedPrefs("address"));

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSharePrefs.setSharedPrefs("order_type", "101");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorsFragment(context)).addToBackStack(null).commit();
            }
        });


        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSharePrefs.setSharedPrefs("order_type", "102");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorsFragment(context)).addToBackStack(null).commit();
            }
        });

        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSharePrefs.setSharedPrefs("order_type", "103");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorsFragment(context)).addToBackStack(null).commit();
            }
        });

        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PopularFragment(context)).addToBackStack(null).commit();
            }
        });


        // if you want show image only / without description text use DefaultSliderView instead


        // initialize SliderLayout

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopCyclingWhenTouch(false);


        LoadSlider slider = new LoadSlider();
        slider.execute(userSharePrefs.getSharedPrefs("city_id"));

        requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        return view;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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


    class LoadSlider extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("status").equals("true")) {
                    JSONArray sliderJsonArray = new JSONArray(jsonObject.getString("data"));

                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < sliderJsonArray.length(); i++) {

                        json_data = sliderJsonArray.getJSONObject(i);
                        String str = context.getResources().getString(R.string.file_base_url) + "slider/" + json_data.getString("image");
                        url_maps.put("", str);
                        for (String name : url_maps.keySet()) {
                            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                            // initialize a SliderLayout
                            defaultSliderView.setProgressBarVisible(true)
                                    .image(url_maps.get(name))
                            ;


                            mDemoSlider.addSlider(defaultSliderView);
                        }
                    }


//                    int size = sliderJsonArray.length();

//                    for (int i = 0; i < size; i++) {
//                        JSONObject sObject = sliderJsonArray.getJSONObject(i);
//                        listUrl.add(image);
//                    }
//                    Toast.makeText(context, "Received " + listUrl.size(), Toast.LENGTH_SHORT).show();
//
//                    for (int i = 0; i < listUrl.size(); i++) {
//
//
//
//                        Toast.makeText(context, "Received " + listUrl.get(i), Toast.LENGTH_SHORT).show();
//
////                        sliderView.image(listUrl.get(i));
////                        mDemoSlider.addSlider(sliderView);
//
//                    }


                }
                processDialog.dismiss();
            } catch (JSONException e) {
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String urls = getString(R.string.base_url).concat("sliders");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
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
