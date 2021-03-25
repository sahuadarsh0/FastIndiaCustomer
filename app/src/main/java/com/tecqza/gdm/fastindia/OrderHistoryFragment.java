package com.tecqza.gdm.fastindia;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class OrderHistoryFragment extends Fragment {
    Context context;
    ProcessDialog processDialog;
    SharedPrefs loginSharedPrefs;
    private View view;
    private ImageView filter;

    private Dialog filter_dialog;
    private TextView start_date, end_date, apply;


    private DatePickerDialog.OnDateSetListener from_dateListener, to_dateListener;
    ArrayList<OrderItemsModel> orders_list;
    private Calendar myCalendar;
    OrderAdapter orderAdapter;

    public OrderHistoryFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        loginSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.order_history_fragment, container, false);

        filter = view.findViewById(R.id.filter);
        myCalendar = Calendar.getInstance();

        from_dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(start_date);
            }
        };
        to_dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(end_date);
            }
        };


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                filter_dialog = new Dialog(context);
                filter_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                filter_dialog.setContentView(R.layout.filter_dialog);
                Window window = filter_dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawableResource(R.color.semi_transparent);

                start_date = filter_dialog.findViewById(R.id.start);
                end_date = filter_dialog.findViewById(R.id.end);
                apply = filter_dialog.findViewById(R.id.apply);
                filter_dialog.setCancelable(true);

                filter_dialog.show();


                start_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DatePickerDialog(context, from_dateListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                end_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(context, to_dateListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                    }
                });
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String startDT, endDT;
                        startDT = start_date.getText().toString() + " 00:00:00";
                        endDT = end_date.getText().toString() + " 23:59:59";

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        try {
                            Date dtStart = format.parse(startDT);
                            Date dtEnd = format.parse(endDT);

                            ArrayList<OrderItemsModel> newList = new ArrayList<>();
                            for (OrderItemsModel order : orders_list) {
                                if (order.cDT.compareTo(dtStart) > 0 && order.cDT.compareTo(dtEnd) < 0) {
                                    newList.add(order);
                                }
                            }
                            orderAdapter.updateList(newList);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        filter_dialog.dismiss();

                    }
                });


            }
        });
//        dateView = view.findViewById(R.id.textView3);


        LoadOrders orders = new LoadOrders();
        orders.execute(loginSharedPrefs.getSharedPrefs("id"));
        return view;

    }

    private void updateLabel(TextView textView) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }


    class LoadOrders extends AsyncTask<String, Void, String> {

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


                    JSONArray ordersJsonArray = new JSONArray(jsonObject.getString("data"));
                    orders_list = OrderItemsModel.fromJson(ordersJsonArray);

                    RecyclerView recyclerView = view.findViewById(R.id.ordersRecyclerView);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    orderAdapter = new OrderAdapter(orders_list, context);
                    recyclerView.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
            }
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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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


