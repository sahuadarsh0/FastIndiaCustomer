package com.tecqza.gdm.fastindia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class SupportFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView submit, text_address, email, call, call2;
    EditText feedback_text;

    public SupportFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.support_fragment, container, false);

        ImageView back = view.findViewById(R.id.back);
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        feedback_text = view.findViewById(R.id.feedback_text);
        submit = view.findViewById(R.id.submit);
        call = view.findViewById(R.id.call);
        call2 = view.findViewById(R.id.call2);
        email = view.findViewById(R.id.email);
        text_address = view.findViewById(R.id.text_address);


        text_address.setText(userSharedPrefs.getSharedPrefs("address"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback_text.getText().toString().equals("")) {
                    Toast.makeText(context, "Enter a message", Toast.LENGTH_SHORT).show();
                } else {
                    SendFeedback feedback = new SendFeedback();
                    feedback.execute(userSharedPrefs.getSharedPrefs("id"), feedback_text.getText().toString());
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18001030770"));
                startActivity(intent);
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        return view;
    }


    void sendEmail() {
        String[] TO = {"support@fastindia.app"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    class SendFeedback extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            try {
                feedback_text.setText("");

                Dialog sent_dialog;
                sent_dialog = new Dialog(context);
                sent_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                sent_dialog.setContentView(R.layout.alert_dialog);
                Window window = sent_dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawableResource(R.color.semi_transparent);
                sent_dialog.setCancelable(true);
                TextView title, message;
                title = sent_dialog.findViewById(R.id.title);
                message = sent_dialog.findViewById(R.id.message);

                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("true")) {
                    title.setText("Success");
                    message.setText(jsonObject.getString("msg"));
                    sent_dialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            sent_dialog.dismiss();
                            getActivity().onBackPressed();
                        }
                    };
                    handler.postDelayed(runnable, 3000);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("feedback/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("feedback", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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