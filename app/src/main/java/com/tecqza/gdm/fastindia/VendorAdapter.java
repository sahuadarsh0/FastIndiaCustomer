package com.tecqza.gdm.fastindia;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.ArrayList;


public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    ArrayList<VendorModel> vendors;
    VendorModel vendor;
    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;

    public VendorAdapter(ArrayList<VendorModel> vendors, Context context) {
        this.vendors = vendors;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "PROCESSING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vendor_list_item, parent, false);
        return new VendorAdapter.VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        vendor = vendors.get(position);

        holder.id.setText(vendor.id);
        holder.name.setText(vendor.business_name);
        holder.address.setText(vendor.address);
        holder.mobile.setText(vendor.mobile);

        if (vendor.open.equals("1")) {
            holder.open_close.setText("OPEN");
            holder.open_close.setTextColor(context.getResources().getColor(R.color.green));


        } else {
            holder.open_close.setText("CLOSED");
            holder.open_close.setTextColor(context.getResources().getColor(R.color.red));
        }

        if (!vendor.favourite_vendor_id.equals("null")) {
            holder.fav.setVisibility(View.INVISIBLE);
            holder.fav_filled.setVisibility(View.VISIBLE);
        } else {
            holder.fav.setVisibility(View.VISIBLE);
            holder.fav_filled.setVisibility(View.INVISIBLE);
        }
        String image_path = context.getString(R.string.file_base_url) + "vendors/" + vendor.image;
        Picasso.get().load(image_path).into(holder.image);


        holder.vendorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.open_close.getText().toString().equals("OPEN")) {
                    userSharedPrefs.setSharedPrefs("vendor_id", holder.id.getText().toString());
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment = new VendorProductsFragment(context);
                    Fragment fragment2 = new AddExtraItemsFragment(context);
                    if (userSharedPrefs.getSharedPrefs("order_type") != null &&userSharedPrefs.getSharedPrefs("order_type").equals("102")) {
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment2).addToBackStack(null).commit();
                    } else {
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
                    }
                } else
                    Toast.makeText(context, "Vendor is Closed", Toast.LENGTH_SHORT).show();
            }
        });

        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fav.setVisibility(View.GONE);
                holder.fav_filled.setVisibility(View.VISIBLE);
                SetFavouriteVendors setFavouriteVendors = new SetFavouriteVendors();
                setFavouriteVendors.execute(userSharedPrefs.getSharedPrefs("id"), holder.id.getText().toString());
            }
        });

        holder.fav_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fav_filled.setVisibility(View.GONE);
                holder.fav.setVisibility(View.VISIBLE);
                UnSetFavouriteVendors unSetFavouriteVendors = new UnSetFavouriteVendors();
                unSetFavouriteVendors.execute(userSharedPrefs.getSharedPrefs("id"), holder.id.getText().toString());
            }
        });
    }


    @Override
    public int getItemCount() {
        return vendors.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout vendorContainer;
        TextView name, address, id, open_close, mobile;
        ImageView image, fav, fav_filled;

        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);
            vendorContainer = itemView.findViewById(R.id.vendorContainer);

            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            open_close = itemView.findViewById(R.id.open_close);
            mobile = itemView.findViewById(R.id.mobile);
            image = itemView.findViewById(R.id.image);
            fav = itemView.findViewById(R.id.fav);
            fav_filled = itemView.findViewById(R.id.fav_filled);

        }
    }


    class SetFavouriteVendors extends AsyncTask<String, Void, String> {

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

                } else {

                }

            } catch (JSONException e) {
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("set_favourite/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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

    class UnSetFavouriteVendors extends AsyncTask<String, Void, String> {

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
                } else {
                }

            } catch (JSONException e) {

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("remove_favourite/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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

    public void updateList(ArrayList<VendorModel> newList) {
        vendors = new ArrayList<>();
        vendors.addAll(newList);
        notifyDataSetChanged();
    }
}
