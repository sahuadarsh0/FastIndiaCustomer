package com.tecqza.gdm.fastindia;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private final Context context;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;
    private TextView edit1, mobile, name1, manual_address, manual_address_text;
    private final BottomNavigationView bottomNavigationView;
    private String state, city, pin;
    private ImageButton gps;

    private NiceSpinner city_spinner, state_spinner;
    private List<String> city_dataset, state_dataset;
    private ArrayList<CityModel> city_list;
    private ArrayList<StateModel> state_list;

    private EditText name, email, address, address2, pin_code;
    private ResultReceiver resultReceiver;

    private TextView t_n_c, delivery_and_shipping, privacy_policy, refund_cancellation, online_registration, product_service, pricing_structure, about_us, support, feedback, rate_us;
    private TextView log_out, notifications, submit, cancel;

    private Dialog logout_dialog, manual_address_dialog;

    double lat, lon;
    private ImageView profile;
    private Uri imageUri;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public SettingsFragment(Context context, BottomNavigationView bottomNavigationView) {
        this.context = context;
        this.processDialog = new ProcessDialog(context, "LOADING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
        this.bottomNavigationView = bottomNavigationView;
    }

    public static int REQUEST_CODE = 200;

    private static final int PERMISSION_REQUEST = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        resultReceiver = new AddressResultReceiver(new Handler());

        ImageView back = view.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE);
        }

        name = view.findViewById(R.id.name);
        name1 = view.findViewById(R.id.name1);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        address2 = view.findViewById(R.id.address2);
        gps = view.findViewById(R.id.gps);
        profile = view.findViewById(R.id.profile);
        notifications = view.findViewById(R.id.notifications);
        manual_address = view.findViewById(R.id.manual_address);
        manual_address_text = view.findViewById(R.id.manual_address_text);


        t_n_c = view.findViewById(R.id.t_n_c);
        delivery_and_shipping = view.findViewById(R.id.delivery_and_shipping);
        privacy_policy = view.findViewById(R.id.privacy_policy);
        refund_cancellation = view.findViewById(R.id.refund_cancellation);
        online_registration = view.findViewById(R.id.online_registration);
        product_service = view.findViewById(R.id.product_service);
        pricing_structure = view.findViewById(R.id.pricing_structure);
        about_us = view.findViewById(R.id.about_us);
        support = view.findViewById(R.id.support);
        feedback = view.findViewById(R.id.feedback);
        rate_us = view.findViewById(R.id.rate_us);
        log_out = view.findViewById(R.id.log_out);
        mobile = view.findViewById(R.id.mobile);


        logout_dialog = new Dialog(context);
        logout_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logout_dialog.setContentView(R.layout.logout_dialog);
        Window window = logout_dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(R.color.semi_transparent);
        logout_dialog.setCancelable(true);


        userSharedPrefs.getSharedPrefs("mobile");
        t_n_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("terms_and_conditions.json");
            }
        });

        delivery_and_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("delivery_and_shipping_policy.json");

            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("privacy_policy.json");

            }
        });

        refund_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("refund_and_cancellations.json");

            }
        });

        online_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("online_registration.json");

            }
        });

        product_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("product_services_details.json");

            }
        });

        pricing_structure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("pricing_structure.json");

            }
        });

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("about_us.json");
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SupportFragment(context)).addToBackStack(null).commit();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FeedbackFragment(context)).addToBackStack(null).commit();
            }
        });

        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }

            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout_dialog.show();

                TextView yes, no;
                yes = logout_dialog.findViewById(R.id.yes);
                no = logout_dialog.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        userSharedPrefs.clearAll();
                        getActivity().finish();
                        Intent i = new Intent(context, Login.class);
                        getActivity().startActivity(i);
                        logout_dialog.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout_dialog.dismiss();
                    }
                });
            }
        });


        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationsFragment(context)).addToBackStack(null).commit();

            }
        });


        TextView upload_text = view.findViewById(R.id.textView111);
        name.setText(userSharedPrefs.getSharedPrefs("name"));
        name1.setText(userSharedPrefs.getSharedPrefs("name"));
        email.setText(userSharedPrefs.getSharedPrefs("email"));
        address.setText(userSharedPrefs.getSharedPrefs("address"));
        address2.setText(userSharedPrefs.getSharedPrefs("address"));
        mobile.setText(userSharedPrefs.getSharedPrefs("mobile"));
        state = userSharedPrefs.getSharedPrefs("state");
        city = userSharedPrefs.getSharedPrefs("city");
        pin = userSharedPrefs.getSharedPrefs("pin");

        if (userSharedPrefs.getSharedPrefs("image") != null) {
            String image_path = context.getString(R.string.file_base_url) + "customers/" + userSharedPrefs.getSharedPrefs("image");
            Picasso.get().load(image_path).placeholder(R.drawable.profile).into(profile);
            upload_text.setVisibility(View.GONE);
        } else upload_text.setVisibility(View.VISIBLE);


        if (userSharedPrefs.getSharedPrefs("pin") != null)
            pin = (userSharedPrefs.getSharedPrefs("pin"));

        if (userSharedPrefs.getSharedPrefs("city") != null)
            city = (userSharedPrefs.getSharedPrefs("city"));

        if (userSharedPrefs.getSharedPrefs("state") != null)
            state = (userSharedPrefs.getSharedPrefs("state"));

        edit1 = view.findViewById(R.id.edit1);

        if (userSharedPrefs.getSharedPrefs("name") == null) {

            edit1.setText("Save");
            enableEditText(name);
            name.setSelection(name.getText().length());
            name.requestFocus();
            enableEditText(email);
            enableEditText(address);
            enableEditText(address2);
            gps.setVisibility(View.VISIBLE);
            manual_address.setVisibility(View.VISIBLE);
            manual_address_text.setVisibility(View.VISIBLE);

        } else {

            disableEditText(name);
            disableEditText(email);
            disableEditText(address);
            disableEditText(address2);
            gps.setVisibility(View.GONE);
            manual_address.setVisibility(View.GONE);
            manual_address_text.setVisibility(View.GONE);

        }

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit1.getText().toString().equals("Save")) {
                    SubmitData submitData = new SubmitData();
                    submitData.execute(
                            userSharedPrefs.getSharedPrefs("mobile"),
                            name.getText().toString(),
                            email.getText().toString(),
                            state,
                            city,
                            pin,
                            address.getText().toString(), String.valueOf(lat), String.valueOf(lon)
                    );
                    edit1.setText("Edit");
                    disableEditText(name);
                    disableEditText(email);

                    disableEditText(address);
                    disableEditText(address2);
                    gps.setVisibility(View.GONE);
                    manual_address.setVisibility(View.GONE);
                    manual_address_text.setVisibility(View.GONE);

                } else if (edit1.getText().toString().equals("Edit")) {
                    edit1.setText("Save");

                    enableEditText(name);
                    name.setSelection(name.getText().length());
                    name.requestFocus();
                    enableEditText(email);
                    enableEditText(address);
                    enableEditText(address2);
                    gps.setVisibility(View.VISIBLE);
                    manual_address.setVisibility(View.VISIBLE);
                    manual_address_text.setVisibility(View.VISIBLE);

                }
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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), SettingsFragment.this);
            }
        });

        manual_address_dialog = new Dialog(context);
        manual_address_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        manual_address_dialog.setContentView(R.layout.manual_address_dialog);
        Window window1 = manual_address_dialog.getWindow();
        window1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window1.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window1.setBackgroundDrawableResource(R.color.semi_transparent);
        manual_address_dialog.setCancelable(true);


        manual_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual_address_dialog.show();

                city_spinner = manual_address_dialog.findViewById(R.id.city_spinner);
                state_spinner = manual_address_dialog.findViewById(R.id.state_spinner);
                pin_code = manual_address_dialog.findViewById(R.id.pin);
                submit = manual_address_dialog.findViewById(R.id.submit);
                cancel = manual_address_dialog.findViewById(R.id.cancel_button);


                LoadStates loadStates = new LoadStates();
                loadStates.execute();

                city_spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                    @Override
                    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {

                        CityModel cityModel = city_list.get(position - 1);
                        city = cityModel.name;
                        userSharedPrefs.setSharedPrefs("city", city);
                    }
                });

                state_spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                    @Override
                    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                        StateModel stateModel = state_list.get(position - 1);
                        LoadCities loadCities = new LoadCities();
                        state = stateModel.name;

                        userSharedPrefs.setSharedPrefs("state", state);
                        loadCities.execute(stateModel.id);
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pin = pin_code.getText().toString();
                        manual_address_dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manual_address_dialog.dismiss();
                    }
                });


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

        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST);
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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(context).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();

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

                Dialog dialog;
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alert_dialog);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawableResource(R.color.semi_transparent);
                dialog.setCancelable(true);
                TextView title, message;
                title = dialog.findViewById(R.id.title);
                message = dialog.findViewById(R.id.message);


                final JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("missing")) {
                    title.setText("Missing Values");
                    StringBuffer errors = new StringBuffer();
                    JSONArray errorsJsonArray = new JSONArray(jsonObject.getString("errors"));
                    JSONObject errorJsonObject;
                    for (int i = 0; i < errorsJsonArray.length(); i++) {
                        errorJsonObject = errorsJsonArray.getJSONObject(i);
                        errors.append(errorJsonObject.getString("error")).append("\n");
                    }
                    message.setText(errors);
                    dialog.show();
                } else if (jsonObject.getString("status").equals("true")) {


                    title.setText("Success");
                    message.setText(jsonObject.getString("msg"));

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
                    dialog.show();

                } else if (jsonObject.getString("status").equals("error")) {
                    title.setText("Undefined");
                    message.setText(jsonObject.getString("msg"));
                    dialog.show();
                } else {
                    title.setText("Error");
                    message.setText(jsonObject.getString("msg"));
                    dialog.show();
                }
                Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                };
                handler.postDelayed(runnable, 2000);
            } catch (JSONException e) {
                Log.d("asa", "onPostExecute: " + e.toString());
                Log.d("asa", "onPostExecute: " + s);
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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("pin", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                        URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&" +
                        URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8");

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


    private void fetchAddressFromLatLong(Location location) {

        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        context.startService(intent);
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

    private void OpenFragment(String url) {

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UrlFragment(context, url)).addToBackStack(null).commit();

    }

    private class AddressResultReceiver extends ResultReceiver {


        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                address.setText(resultData.getString(Constants.RESULT_DATA_KEY));

                state = resultData.getString(Constants.STATE);
                city = resultData.getString(Constants.CITY);
                pin = resultData.getString(Constants.PIN);


                userSharedPrefs.setSharedPrefs("state", state);
                userSharedPrefs.setSharedPrefs("city", city);
                userSharedPrefs.setSharedPrefs("pin", pin);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    profile.setImageBitmap(bitmap);

                    UploadProfileImage uploadProfileImage = new UploadProfileImage();
                    uploadProfileImage.execute();

                } catch (IOException e) {
                    Log.d("sdr:", e.toString());
                }
            }
        }
    }


    class UploadProfileImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            uploadMultipart();
            return null;
        }
    }

    public void uploadMultipart() {

        String urls = context.getString(R.string.base_url).concat("/upload_customer_image/");

        //Uploading code
        Random rand = new Random();
        int random_no = rand.nextInt(9999999);
        String image_name = userSharedPrefs.getSharedPrefs("mobile") + random_no + ".jpg";
        userSharedPrefs.setSharedPrefs("image", image_name);
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            new MultipartUploadRequest(context, uploadId, urls)
                    .addFileToUpload(imageUri.getPath(), "image") //Adding file
                    .addParameter("name", image_name) //Adding text parameter to the request
                    .addParameter("customer_id", userSharedPrefs.getSharedPrefs("id")) //Adding text parameter to the request

                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    class LoadStates extends AsyncTask<String, Void, String> {

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (jsonObject.getString("status").equals("true")) {
                    JSONArray stateJsonArray = new JSONArray(jsonObject.getString("data"));

                    state_list = StateModel.fromJson(stateJsonArray);
                    List<String> tempset;
                    tempset = (Arrays.asList("Select State"));
                    state_dataset = new ArrayList<>();
                    state_dataset.addAll(tempset);
                    for (int i = 0; i < state_list.size(); i++) {
                        tempset = (Arrays.asList(state_list.get(i).name));
                        state_dataset.addAll(tempset);

                    }
                    state_spinner.attachDataSource(state_dataset);

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
            String urls = context.getString(R.string.base_url).concat("states/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

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


    class LoadCities extends AsyncTask<String, Void, String> {

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (jsonObject.getString("status").equals("true")) {
                    JSONArray citiesJsonArray = new JSONArray(jsonObject.getString("data"));

                    city_list = CityModel.fromJson(citiesJsonArray);
                    List<String> tempset;
                    tempset = (Arrays.asList("Select City"));

                    city_dataset = new ArrayList<>();
                    city_dataset.addAll(tempset);
                    for (int i = 0; i < city_list.size(); i++) {
                        tempset = (Arrays.asList(city_list.get(i).name));
                        city_dataset.addAll(tempset);

                    }
                    city_spinner.attachDataSource(city_dataset);

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
            String urls = context.getString(R.string.base_url).concat("cities/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("state_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
