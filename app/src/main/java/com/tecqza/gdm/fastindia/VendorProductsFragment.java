package com.tecqza.gdm.fastindia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;

public class VendorProductsFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    View view;
    TextView add_item, total, clear_selection;
    DatabaseHelper myDb;
    ConstraintLayout done;

    TextView vendor_name, vendor_mobile, vendor_address, text_address, items_selected;
    ImageView vendor_image;

    EditText searchProduct;
    VendorProductsAdapter vendorProductsAdapter;
    ArrayList<ProductModel> product_list;

    public VendorProductsFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        userSharePrefs = new SharedPrefs(context, "USER");

        myDb = new DatabaseHelper(context);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.vendor_products_fragments, container, false);

        vendor_name = view.findViewById(R.id.vendor_name);
        vendor_mobile = view.findViewById(R.id.vendor_mobile);
        vendor_address = view.findViewById(R.id.vendor_address);
        vendor_image = view.findViewById(R.id.vendor_image);
        add_item = view.findViewById(R.id.add_item);
        total = view.findViewById(R.id.total);
        clear_selection = view.findViewById(R.id.clear_selection);
        items_selected = view.findViewById(R.id.items_selected);

        done = view.findViewById(R.id.done);
        text_address = view.findViewById(R.id.text_address);
        searchProduct = view.findViewById(R.id.searchProduct);
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });

        text_address.setText(userSharePrefs.getSharedPrefs("address"));

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<ProductModel> newList = new ArrayList<>();
                for (ProductModel product : product_list) {
                    if (product.name.toLowerCase().contains(searchProduct.getText().toString())) {
                        newList.add(product);
                    }
                }
                vendorProductsAdapter.updateList(newList);
            }
        });


        LoadVendorsProduct loadVendorsProduct = new LoadVendorsProduct();
        loadVendorsProduct.execute(userSharePrefs.getSharedPrefs("vendor_id"));

        Cursor res = myDb.getNonExtraItems(userSharePrefs.getSharedPrefs("vendor_id"));

        clear_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.deleteWhereVendor(userSharePrefs.getSharedPrefs("vendor_id"));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorProductsFragment(context)).addToBackStack(null).commit();
            }
        });

        myDb = new DatabaseHelper(context);
        Cursor res1 = myDb.countNonExtra(userSharePrefs.getSharedPrefs("vendor_id"));
        if (res1.getCount() > 0) {
            while (res1.moveToNext()) {
                items_selected.setText(res1.getString(0));
            }
        }


        float total_amt = 0.0f;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                int qty = Integer.parseInt(res.getString(3));
                float amount = Float.parseFloat(res.getString(8));
                total_amt = total_amt + (amount * qty);
            }
        }
        total.setText(total_amt + "");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item = 0;
                Cursor res1 = myDb.count(userSharePrefs.getSharedPrefs("vendor_id"));
                if (res1.getCount() > 0) {
                    while (res1.moveToNext()) {
                        item = Integer.parseInt(res1.getString(0));
                    }
                }

                if (item > 0)
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddExtraItemsFragment(context)).addToBackStack(null).commit();
                else
                    Toast.makeText(context, "Add Products to the Cart", Toast.LENGTH_SHORT).show();
            }

        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog;
                dialog = new Dialog(context, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                dialog.setContentView(R.layout.input_item_dialog);
                dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_base);
                dialog.show();

                final EditText product_name, product_qty;
                TextView add_button, cancel_button;

                product_name = dialog.findViewById(R.id.product_name);
                product_qty = dialog.findViewById(R.id.product_qty);

                add_button = dialog.findViewById(R.id.add_button);
                cancel_button = dialog.findViewById(R.id.cancel_button);

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String p_name, p_qty;
                        p_name = product_name.getText().toString();
                        p_qty = product_qty.getText().toString();

                        if (p_name.equals("")) {
                            Toast.makeText(context, "Product name missing", Toast.LENGTH_SHORT).show();
                        } else if (p_qty.equals("")) {
                            Toast.makeText(context, "Product qty missing", Toast.LENGTH_SHORT).show();
                        } else {
                            myDb.insertExtraItem(p_name, p_qty, userSharePrefs.getSharedPrefs("vendor_id"));
                            dialog.dismiss();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddExtraItemsFragment(context)).addToBackStack(null).commit();
                        }
                    }
                });
            }
        });
        return view;
    }


    class LoadVendorsProduct extends AsyncTask<String, Void, String> {

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

                    JSONObject vendor = new JSONObject(jsonObject.getString("vendor"));

                    vendor_name.setText(vendor.getString("business_name"));
                    vendor_mobile.setText(vendor.getString("mobile"));
                    vendor_address.setText(vendor.getString("address"));
                    userSharePrefs.setSharedPrefs("order_type", vendor.getString("type"));

                    String image_path = context.getString(R.string.file_base_url) + "vendors/" + vendor.getString("image");
                    Picasso.get().load(image_path).into(vendor_image);


                    JSONArray productJsonArray = new JSONArray(jsonObject.getString("products"));
                    product_list = ProductModel.fromJson(productJsonArray, context);


                    RecyclerView productRecyclerView = view.findViewById(R.id.productRecyclerView);
                    productRecyclerView.clearOnScrollListeners();
                    productRecyclerView.setNestedScrollingEnabled(true);
                    productRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    vendorProductsAdapter = new VendorProductsAdapter(product_list, context, total, items_selected);
                    productRecyclerView.setAdapter(vendorProductsAdapter);
                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("vendor_products/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
