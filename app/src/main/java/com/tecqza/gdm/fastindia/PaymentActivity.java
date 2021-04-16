package com.tecqza.gdm.fastindia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

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

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    ProcessDialog processDialog;
    String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Checkout.preload(this);
        processDialog = new ProcessDialog(this, "LOADING");
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String amount = bundle.getString("amount", null);
        order_id = bundle.getString("order_id", null);

        startPayment(amount);

    }

    public void startPayment(String amount) {

        Checkout checkout = new Checkout();

        checkout.setImage(R.drawable.fast_inida);

        AppCompatActivity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Fast India");

            options.put("currency", "INR");

            options.put("amount", amount);


            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {

        RecordPayment recordPayment = new RecordPayment();
        recordPayment.execute(s, order_id);


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
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                if (jsonObject.getString("status").equals("true")) {

                    finish();

                } else {
                    builder.setTitle("Error");
                    builder.setMessage(jsonObject.getString("msg"));
                    builder.show();
                }
            } catch (JSONException e) {

            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls =
                    getString(R.string.base_url).concat("recordPostPayment/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("payment_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("order_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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