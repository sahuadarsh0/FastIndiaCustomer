package com.tecqza.gdm.fastindia;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
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
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    Button save;
    BottomNavigationView bottomNavigationView;
    String state, city, pin;
    ImageButton gps;

    EditText name, email, address;
    private ResultReceiver resultReceiver;


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public ProfileFragment(Context context, BottomNavigationView bottomNavigationView) {
        this.context = context;
        this.processDialog = new ProcessDialog(context, "LOADING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
        this.bottomNavigationView = bottomNavigationView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        resultReceiver = new AddressResultReceiver(new Handler());


        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        gps = view.findViewById(R.id.gps);

        name.setText(userSharedPrefs.getSharedPrefs("name"));
        email.setText(userSharedPrefs.getSharedPrefs("email"));
        address.setText(userSharedPrefs.getSharedPrefs("address"));


        save = view.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitData submitData = new SubmitData();
                submitData.execute(
                        userSharedPrefs.getSharedPrefs("mobile"),
                        name.getText().toString(),
                        email.getText().toString(),
                        state,
                        city,
                        pin,
                        address.getText().toString()
                );
            }
        });


        Activity activity = (Activity) view.getContext();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else getCurrentLocation();

            }
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(context).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            address.setText(String.format("Latitude : %s\n Longitude: %s", lat, lon));
                            Location location = new Location("providerNA");
                            location.setLatitude(lat);
                            location.setLongitude(lon);
                            fetchAddressFromLatLong(location);
                        } else {

                        }
                    }
                }, Looper.getMainLooper());


    }


    class SubmitData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            try {
                final JSONObject jsonObject = new JSONObject(s);
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
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            processDialog.dismiss();
                            try {
                                JSONObject customer_JO = new JSONObject(jsonObject.getString("data"));

                                userSharedPrefs.setSharedPrefs("id", customer_JO.getString("id"));
                                userSharedPrefs.setSharedPrefs("name", customer_JO.getString("name"));
                                userSharedPrefs.setSharedPrefs("email", customer_JO.getString("email"));
                                userSharedPrefs.setSharedPrefs("mobile", customer_JO.getString("mobile"));
                                userSharedPrefs.setSharedPrefs("address", customer_JO.getString("address"));
                                userSharedPrefs.setSharedPrefs("state_id", customer_JO.getString("state_id"));
                                userSharedPrefs.setSharedPrefs("city_id", customer_JO.getString("city_id"));

                                bottomNavigationView.setSelectedItemId(R.id.home);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderTypeFragment(context, bottomNavigationView)).commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    builder.show();

                } else if (jsonObject.getString("status").equals("error")) {
                    builder.setTitle("Undefined");
                    builder.setMessage(jsonObject.getString("msg"));
                    builder.show();
                } else {
                    builder.setTitle("Error");
                    builder.setMessage(jsonObject.getString("msg"));
                    builder.show();
                }
            } catch (JSONException e) {
                Log.d("asa", "onPostExecute: " + e.toString());
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("register_customer/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8");

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


    private void fetchAddressFromLatLong(Location location) {

        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        context.startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {


        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                address.setText(resultData.getString(Constants.RESULT_DATA_KEY));

//                int city_index=getIndexOf(state_dataset, resultData.getString(Constants.STATE));
                state = resultData.getString(Constants.STATE);
                city = resultData.getString(Constants.CITY);
                pin = resultData.getString(Constants.PIN);
            }
        }
    }

}
