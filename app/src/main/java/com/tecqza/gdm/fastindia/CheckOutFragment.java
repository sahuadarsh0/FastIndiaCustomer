package com.tecqza.gdm.fastindia;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

public class CheckOutFragment extends Fragment implements PaymentResultListener {

    Button order_now;
    ProcessDialog processDialog;
    Context context;
    SharedPrefs userSharedPrefs;
    DatabaseHelper myDb;
    String image_name, is_also_image;
    Bundle bundle;
    EditText address, landmark;
    View view;
    StringBuffer ordered_item_json;
    String is_image_uploaded;
    Boolean is_extra_items;

    public CheckOutFragment(Context context){
        this.context=context;
        processDialog=new ProcessDialog(context,"");
        userSharedPrefs=new SharedPrefs(context, "USER");
        myDb = new DatabaseHelper(context);
        is_also_image="NO";
        is_image_uploaded="NO";
        is_extra_items=false;

        Checkout.preload(context.getApplicationContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.check_out_fragment, container, false);

        address=view.findViewById(R.id.address);
        landmark=view.findViewById(R.id.landmark);

        if(userSharedPrefs.getSharedPrefs("address")!=null){
            address.setText(userSharedPrefs.getSharedPrefs("address"));
        }

        order_now = view.findViewById(R.id.order_now);

        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address.getText().toString().equals("")){
                    Toast.makeText(context, "Address is mandetory field", Toast.LENGTH_SHORT).show();
                }else if(landmark.getText().toString().equals("")){
                    Toast.makeText(context, "Landmark is mandetory field", Toast.LENGTH_SHORT).show();
                }else {

                    ordered_item_json = new StringBuffer();
                    Cursor res = myDb.getAllItemsOfVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                    ordered_item_json.append("[");
                    int i = 0;
                    float total_amt=0.0f;
                    if (res.getCount() > 0) {
                        while (res.moveToNext()) {
                            i++;
                            ordered_item_json.append("{");
                            ordered_item_json.append("\"vpid\":" + "\"" + res.getString(1) + "\",");
                            ordered_item_json.append("\"name\":" + "\"" + res.getString(2) + "\",");
                            ordered_item_json.append("\"qty\":" + "\"" + res.getString(3) + "\",");
                            ordered_item_json.append("\"extra\":" + "\"" + res.getString(4) + "\",");
                            ordered_item_json.append("\"vendor_id\":" + "\"" + res.getString(5) + "\",");
                            ordered_item_json.append("\"extra_qty\":" + "\"" + res.getString(6) + "\",");
                            ordered_item_json.append("\"image\":" + "\"" + res.getString(7) + "\",");
                            ordered_item_json.append("\"amount\":" + "\"" + res.getString(8) + "\"");
                            if (i < res.getCount()) {
                                ordered_item_json.append("},");
                            } else {
                                ordered_item_json.append("}");
                            }

                            if (res.getString(7).equals("1")) {
                                is_also_image = "YES";
                                image_name=res.getString(2);
                            }
                            if (res.getString(4).equals("1")) {
                                is_extra_items=true;
                            }
                            int qty=Integer.parseInt(res.getString(3));
                            float amount=Float.parseFloat(res.getString(8));
                            total_amt=total_amt+(amount*qty);

                        }
                    }
                    ordered_item_json.append("]");

                    if(is_extra_items){
                        if(is_also_image.equals("YES") && is_image_uploaded.equals("NO")){
                            UploadListImage uploadListImage = new UploadListImage();
                            uploadListImage.execute(image_name);
                        }else{
                            submission();
                        }
                    }else{

                        startPayment((total_amt*1000)+"");
                    }



                }

            }
        });

        return view;
    }

    public void startPayment(String amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.tecqza_logo);

        /**
         * Reference to current activity
         */
        AppCompatActivity activity=(AppCompatActivity) view.getContext();

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Shashwat Publication");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */


            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", amount);

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(context, "Error on paymnet", Toast.LENGTH_SHORT).show();
    }

    class UploadListImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            submission();
        }
        @Override
        protected String doInBackground(String... params) {
            uploadMultipart(params[0]);
            return null;
        }
    }

    public void uploadMultipart(String image_name) {
        String basePath = Environment.getExternalStorageDirectory().toString();
        File file = new File(basePath, "OrderList.jpg");

        String path = file.getPath();
        String urls = context.getString(R.string.base_url).concat("/upload_list_image/");
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(context, uploadId, urls)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", image_name) //Adding text parameter to the request

                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void submission(){

        userSharedPrefs.setSharedPrefs("address",address.getText().toString());

        SubmitData submitData = new SubmitData();
        submitData.execute(
                userSharedPrefs.getSharedPrefs("id"),
                userSharedPrefs.getSharedPrefs("vendor_id"),
                userSharedPrefs.getSharedPrefs("order_type"),
                userSharedPrefs.getSharedPrefs("state_id"),
                userSharedPrefs.getSharedPrefs("city_id"),
                address.getText().toString(),
                landmark.getText().toString(),
                ordered_item_json.toString()
        );
    }

    class SubmitData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (jsonObject.getString("status").equals("missing")) {
                    builder.setTitle("Missing Values");
                    StringBuffer errors = new StringBuffer();
                    JSONArray errorsJsonArray = new JSONArray(jsonObject.getString("errors"));
                    JSONObject errorJsonObject;
                    for (int i = 0; i < errorsJsonArray.length(); i++) {
                        errorJsonObject = errorsJsonArray.getJSONObject(i);
                        errors.append(errorJsonObject.getString("error")).append("\n");
                    }
                    builder.setMessage(errors);
                    builder.show();
                } else if (jsonObject.getString("status").equals("true")) {
                    builder.setTitle("Success");
                    builder.setMessage(jsonObject.getString("msg"));
                    builder.setCancelable(false);
                    myDb.deleteWhereVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                          getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderPlacedFragment(context)).addToBackStack(null).commit();
                        }
                    });
                    builder.show();
                } else {
                    builder.setTitle("Error");
                    builder.setMessage(jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("place_customer_order/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("order_type", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("state_id", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8")+ "&" +
                        URLEncoder.encode("landmark", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8")+ "&" +
                        URLEncoder.encode("ordered_items", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8");

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
