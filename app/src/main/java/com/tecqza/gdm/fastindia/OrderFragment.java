package com.tecqza.gdm.fastindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;


public class OrderFragment extends Fragment {


    private final boolean allowRefresh = false;
    DatabaseHelper myDb;
    private OrderItemsModel order;
    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    View view;
    private final String order_id;
    private TextView total;

    private ImageView vendor_image;
    private TextView vendor_name, vendor_mobile, vendor_address;
    LinearLayout extra_label;
    TextView text_address;
    ConstraintLayout waiting, confirmed, view_all_history;
    TextView total_amount;
    TextView cod, pay_now;

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
        total_amount = view.findViewById(R.id.total_amount);
        waiting = view.findViewById(R.id.waiting);
        confirmed = view.findViewById(R.id.confirmed);
        extra_label = view.findViewById(R.id.extra_label);
        text_address.setText(userSharePrefs.getSharedPrefs("address"));
        view_all_history = view.findViewById(R.id.constraintLayout2);
        cod = view.findViewById(R.id.cod);
        pay_now = view.findViewById(R.id.pay_now);

        OrdersDetail detail = new OrdersDetail();
        detail.execute(order_id);

        view_all_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderHistoryFragment(context)).addToBackStack(null).commit();
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderDetailsFragment(context, order_id)).addToBackStack(null).commit();

            }
        });

        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float total = Float.parseFloat(total_amount.getText().toString());
                total *= 100;
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("amount", String.valueOf(total));
                intent.putExtra("order_id", order_id);
                startActivity(intent);
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
                    total_amount.setText(order.total_amount);

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


                    if (order.payment_status.equals("PAID")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderDetailsFragment(context, order_id)).addToBackStack(null).commit();
                    }

                    if (order.status.equals("PREPARED")) {
                        confirmed.setVisibility(View.VISIBLE);
                        waiting.setVisibility(View.GONE);
                    }
                    if (order_extra_item_list.size() == 0) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderDetailsFragment(context, order_id)).addToBackStack(null).commit();
                        extra_label.setVisibility(View.GONE);
                        recyclerView1.setVisibility(View.GONE);
                    }
                    if (order.status.equals("DELIVERED") || order.status.equals("REJECTED")) {
                        userSharePrefs.clearByName("order_id");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderHistoryFragment(context)).commit();
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
