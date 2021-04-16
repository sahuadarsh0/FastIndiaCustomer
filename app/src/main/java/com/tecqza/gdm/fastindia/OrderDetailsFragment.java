package com.tecqza.gdm.fastindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;


public class OrderDetailsFragment extends Fragment {


    DatabaseHelper myDb;
    private OrderItemsModel order;
    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    View view;
    private final String order_id;
    private String delivery_boy_mobile;
    private TextView total;

    private ImageView vendor_image;
    private TextView vendor_name, vendor_mobile, vendor_address;
    LinearLayout extra_label;
    TextView text_address, phone, date, payment_mode, order_no, call_delivery_boy, call_vendor, customer_address;
    TextView status;
    TextView pay_now;
    float amount;

    ConstraintLayout view_all_history;

    public OrderDetailsFragment(Context context, String order_id) {
        this.context = context;
        this.processDialog = new ProcessDialog(context, "LOADING");
        this.userSharePrefs = new SharedPrefs(context, "USER");
        this.order_id = order_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.order_details_fragment, container, false);

        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        extra_label = view.findViewById(R.id.extra_label);
        myDb = new DatabaseHelper(context);
        vendor_name = view.findViewById(R.id.vendor_name);
        vendor_mobile = view.findViewById(R.id.vendor_mobile);
        vendor_address = view.findViewById(R.id.vendor_address);
        vendor_image = view.findViewById(R.id.vendor_image);
        text_address = view.findViewById(R.id.text_address);
        total = view.findViewById(R.id.total);
        text_address.setText(userSharePrefs.getSharedPrefs("address"));
        phone = view.findViewById(R.id.phone);
        date = view.findViewById(R.id.date);
        payment_mode = view.findViewById(R.id.payment_mode);
        order_no = view.findViewById(R.id.order_no);
        call_delivery_boy = view.findViewById(R.id.call_delivery_boy);
        call_vendor = view.findViewById(R.id.call_vendor);
        customer_address = view.findViewById(R.id.customer_address);
        status = view.findViewById(R.id.status);
        pay_now = view.findViewById(R.id.pay_now);

        view_all_history = view.findViewById(R.id.constraintLayout2);

        view_all_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderHistoryFragment(context)).addToBackStack(null).commit();
            }
        });
        OrdersDetail detail = new OrdersDetail();
        detail.execute(order_id);

        call_delivery_boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.getText().toString().equals("ALLOTED")) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+91" + delivery_boy_mobile));
                    startActivity(callIntent);
                } else
                    Toast.makeText(context, "Delivery Boy not allotted yet !!!", Toast.LENGTH_SHORT).show();
            }
        });
        call_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+91" + vendor_mobile.getText().toString()));
                startActivity(callIntent);
            }
        });


        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentActivity.class);

                intent.putExtra("amount", String.valueOf(amount*100));
                intent.putExtra("order_id", order_id);
                startActivityForResult(intent,1);
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

                    phone.setText(order.customer_mobile);
                    date.setText(order.cdt);
                    payment_mode.setText(order.payment_mode);
                    order_no.setText(order.id);
                    customer_address.setText(order.customer_address);
                    status.setText(order.status);

                    if (order.payment_mode.equals("COD")) {
                        pay_now.setVisibility(View.VISIBLE);
                    }


                    if (status.getText().toString().equals("PENDING")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            status.setTextColor(context.getResources().getColor(R.color.yellow, null));
                        }
                    } else if (status.getText().toString().equals("REJECTED")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            status.setTextColor(context.getResources().getColor(R.color.red, null));
                        }
                    } else if (status.getText().toString().equals("ACCEPTED")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            status.setTextColor(context.getResources().getColor(R.color.glo_green, null));
                        }
                    } else if (status.getText().toString().equals("DELIVERED")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            status.setTextColor(context.getResources().getColor(R.color.blue, null));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            status.setTextColor(context.getResources().getColor(R.color.blue, null));
                        }
                    }

                    amount = Float.parseFloat(order.total_amount);
                    total.setText("Rs. " + amount);
                    if (!order.status.equals("ALLOTED")) {
                        call_delivery_boy.setVisibility(View.INVISIBLE);
                    }

                    delivery_boy_mobile = order.delivery_boy_mobile;


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

                    if (order_extra_item_list.size() == 0) {
                        extra_label.setVisibility(View.GONE);
                        recyclerView1.setVisibility(View.GONE);
                        Log.d("asa", "onPostExecute: ");
                    }

                }
            } catch (JSONException | ParseException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderDetailsFragment(context,order_id)).commit();

    }
}

