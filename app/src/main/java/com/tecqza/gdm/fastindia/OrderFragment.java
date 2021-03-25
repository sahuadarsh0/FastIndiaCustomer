package com.tecqza.gdm.fastindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
import java.text.ParseException;
import java.util.ArrayList;


public class OrderFragment extends Fragment {


    DatabaseHelper myDb;
    private OrderItemsModel order;
    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    View view;
    private String order_id;
    private TextView total;

    private ImageView vendor_image;
    private TextView vendor_name, vendor_mobile, vendor_address;
    LinearLayout extra_label;
    TextView text_address;
    ConstraintLayout waiting, confirmed, view_all_history;
    TextView status;

    public OrderFragment(Context context, String order_id) {
        this.context = context;
        this.processDialog = new ProcessDialog(context, "LOADING");
        this.userSharePrefs = new SharedPrefs(context, "USER");
        this.order_id = order_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.order_fragment, container, false);

        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        myDb = new DatabaseHelper(context);
        vendor_name = view.findViewById(R.id.vendor_name);
        vendor_mobile = view.findViewById(R.id.vendor_mobile);
        vendor_address = view.findViewById(R.id.vendor_address);
        vendor_image = view.findViewById(R.id.vendor_image);
        text_address = view.findViewById(R.id.text_address);
        total = view.findViewById(R.id.total);
        waiting = view.findViewById(R.id.waiting);
        confirmed = view.findViewById(R.id.confirmed);
        extra_label = view.findViewById(R.id.extra_label);
        text_address.setText(userSharePrefs.getSharedPrefs("address"));
        view_all_history = view.findViewById(R.id.constraintLayout2);

        OrdersDetail detail = new OrdersDetail();
        detail.execute(order_id);

        view_all_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderHistoryFragment(context)).addToBackStack(null).commit();
            }
        });
        return view;
    }


    @SuppressLint("StaticFieldLeak")
    class OrdersDetail extends AsyncTask<String, Void, String> {

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
                String getStatus = jsonObject.getString("status");
                if (getStatus.trim().equals("true")) {

                    JSONObject object = new JSONObject(jsonObject.getString("order"));
                    order = new OrderItemsModel(object);
                    vendor_name.setText(order.vendor_name);
                    vendor_mobile.setText(order.vendor_mobile);
                    vendor_address.setText(order.vendor_address);

                    String image_path = context.getString(R.string.file_base_url) + "vendors/" + order.vendor_image;
                    Picasso.get().load(image_path).into(vendor_image);


                    JSONArray orderedItemsJsonArray = new JSONArray(jsonObject.getString("ordered_items"));
                    JSONArray orderedExtraItemsJsonArray = new JSONArray(jsonObject.getString("ordered_extra_items"));

                    ArrayList<OrderedItemModel> order_item_list = OrderedItemModel.fromJson(orderedItemsJsonArray);
                    ArrayList<OrderedItemModel> order_extra_item_list = OrderedItemModel.fromJson(orderedExtraItemsJsonArray);


                    RecyclerView recyclerView = view.findViewById(R.id.recycler_non_extra);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new OrderDetailsItemsAdapter(order_item_list, context));

                    RecyclerView recyclerView1 = view.findViewById(R.id.recycler_extra);
                    recyclerView1.clearOnScrollListeners();
                    recyclerView1.setNestedScrollingEnabled(true);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView1.setAdapter(new OrderDetailsExtraItemsAdapter(order_extra_item_list, context, total));

//                    customer_name.setText(order.customer_name);
//                    date.setText(order.cdt);
//                    Float total_amount=Float.parseFloat(order.amount)+Float.parseFloat(order.extra_amount);
//                    total.setText(total_amount+"");
//                    status.setText(order.status);

                    if (order.status.equals("PREPARED")) {
                        confirmed.setVisibility(View.VISIBLE);
                        waiting.setVisibility(View.GONE);
                    }
                    if (order_extra_item_list.size() == 0) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderDetailsFragment(context, order_id)).addToBackStack(null).commit();
                        extra_label.setVisibility(View.GONE);
                        recyclerView1.setVisibility(View.GONE);
                    }
                    if (order.status.equals("DELIVERED")) {
                        userSharePrefs.clearByName("order_id");
                    }
//
                }
            } catch (JSONException | ParseException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("order_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
