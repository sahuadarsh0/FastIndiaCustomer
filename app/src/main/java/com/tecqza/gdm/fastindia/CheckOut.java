package com.tecqza.gdm.fastindia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

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
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class CheckOut extends AppCompatActivity implements PaymentResultListener {

    TextView order_now;
    ProcessDialog processDialog;
    Context context;
    SharedPrefs userSharedPrefs;
    DatabaseHelper myDb;
    String image_name, is_also_image;
    EditText address, landmark;
    StringBuffer ordered_item_json;
    String is_image_uploaded;
    Boolean is_extra_items;
    Dialog payment_mode_dialog;

    ImageView vendor_image;
    TextView vendor_name, vendor_mobile, vendor_address;

    TextView text_address;
    TextView change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        ImageView chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        text_address = findViewById(R.id.text_address);
        context = this;
        processDialog = new ProcessDialog(context, "");
        userSharedPrefs = new SharedPrefs(context, "USER");
        myDb = new DatabaseHelper(context);
        is_also_image = "NO";
        is_image_uploaded = "NO";
        is_extra_items = false;


        payment_mode_dialog = new Dialog(context);
        payment_mode_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payment_mode_dialog.setContentView(R.layout.payment_mode_dialog);
        Window window = payment_mode_dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(R.color.semi_transparent);
        payment_mode_dialog.setCancelable(true);


        Checkout.preload(context.getApplicationContext());


        address = findViewById(R.id.address);
        landmark = findViewById(R.id.landmark);
        vendor_name = findViewById(R.id.vendor_name);
        vendor_image = findViewById(R.id.vendor_image);
        vendor_mobile = findViewById(R.id.vendor_mobile);
        vendor_address = findViewById(R.id.vendor_address);
        change = findViewById(R.id.change);

        if (userSharedPrefs.getSharedPrefs("address") != null) {
            address.setText(userSharedPrefs.getSharedPrefs("address"));
            text_address.setText(userSharedPrefs.getSharedPrefs("address"));
        }

        if (userSharedPrefs.getSharedPrefs("landmark") != null) {
            landmark.setText(userSharedPrefs.getSharedPrefs("landmark"));
        }

        order_now = findViewById(R.id.order_now);

        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (address.getText().toString().equals("")) {
                    Toast.makeText(context, "Address is mandatory field", Toast.LENGTH_SHORT).show();
                } else if (landmark.getText().toString().equals("")) {
                    Toast.makeText(context, "Landmark is mandatory field", Toast.LENGTH_SHORT).show();
                } else {
                    userSharedPrefs.setSharedPrefs("landmark", landmark.getText().toString());
                    ordered_item_json = new StringBuffer();
                    Cursor res = myDb.getAllItemsOfVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                    ordered_item_json.append("[");
                    int i = 0;
                    float total_amt = 0.0f;
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
                                image_name = res.getString(2);
                            }
                            if (res.getString(4).equals("1")) {
                                is_extra_items = true;
                            } else {
                                int qty = Integer.parseInt(res.getString(3));
                                float amount = Float.parseFloat(res.getString(8));
                                total_amt = total_amt + (amount * qty);
                            }




                        }
                    }
                    ordered_item_json.append("]");

                    if (total_amt > 100) {
                        if (is_extra_items) {
                            if (is_also_image.equals("YES") && is_image_uploaded.equals("NO")) {
                                UploadListImage uploadListImage = new UploadListImage();
                                uploadListImage.execute(image_name);
                            } else {
                                submission("NA");
                            }
                        } else {
                            payment_mode_dialog.show();

                            TextView cod, pay_now;
                            cod = payment_mode_dialog.findViewById(R.id.cod);
                            pay_now = payment_mode_dialog.findViewById(R.id.pay_now);
                            cod.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    submission("NA");
                                }
                            });
                            final float tm = total_amt;
                            pay_now.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startPayment((tm * 100) + "");
                                }
                            });

                        }
                    } else {
                        Toast.makeText(context, "Billing amount should be at least 100 Rupees.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });



        if (userSharedPrefs.getSharedPrefs("vendor_image") != null) {
            String image_path = context.getString(R.string.file_base_url) + "vendors/" + userSharedPrefs.getSharedPrefs("vendor_image");
            Picasso.get().load(image_path).into(vendor_image);
            vendor_mobile.setText(userSharedPrefs.getSharedPrefs("vendor_mobile"));
            vendor_name.setText(userSharedPrefs.getSharedPrefs("business_name"));
            vendor_address.setText(userSharedPrefs.getSharedPrefs("vendor_address"));
        }
        disableEditText(landmark);
        disableEditText(address);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change.getText().toString().equals("Save")) {
                    change.setText("Change");
                    disableEditText(landmark);
                    disableEditText(address);
                } else if (change.getText().toString().equals("Change")) {
                    change.setText("Save");
                    enableEditText(address);
                    address.setSelection(address.getText().length());
                    address.requestFocus();
                    enableEditText(landmark);
                    enableEditText(address);
                }
            }
        });

    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
        editText.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (processDialog != null) {
            processDialog.dismiss();
            processDialog = null;
        }
    }


    public void startPayment(String amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.fast_inida);

        /**
         * Reference to current activity
         */
        AppCompatActivity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Fast India");

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
        } catch (Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        RecordPayment recordPayment = new RecordPayment();
        recordPayment.execute(s);
    }


    @Override
    public void onPaymentError(int i, String s) {

    }

    class RecordPayment extends AsyncTask<String, Void, String> {

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
                if (jsonObject.getString("status").equals("true")) {
                    submission(jsonObject.getString("id"));
                } else {
                    builder.setTitle("Error");
                    builder.setMessage(jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
//                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("recordPayment/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("payment_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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


    class UploadListImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            submission("NA");
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
//            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void submission(String payment_id) {

        userSharedPrefs.setSharedPrefs("address", address.getText().toString());

        SubmitData submitData = new SubmitData();
        submitData.execute(
                userSharedPrefs.getSharedPrefs("id"),
                userSharedPrefs.getSharedPrefs("vendor_id"),
                userSharedPrefs.getSharedPrefs("order_type"),
                userSharedPrefs.getSharedPrefs("state_id"),
                userSharedPrefs.getSharedPrefs("city_id"),
                address.getText().toString(),
                landmark.getText().toString(),
                ordered_item_json.toString(),
                payment_id
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

//                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                } else if (jsonObject.getString("status").equals("true")) {

                    String order_id = jsonObject.getString("order_id");
                    userSharedPrefs.setSharedPrefs("order_id", order_id);
                    myDb.deleteWhereVendor(userSharedPrefs.getSharedPrefs("vendor_id"));

                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("open", "ORDER_PLACED");
                    intent.putExtra("order_id", order_id);
                    startActivity(intent);
                    finish();

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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("order_type", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("state_id", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                        URLEncoder.encode("landmark", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                        URLEncoder.encode("ordered_items", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&" +
                        URLEncoder.encode("payment_id", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8");

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
