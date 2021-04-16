package com.tecqza.gdm.fastindia;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;

public class AddExtraItemsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private final Context context;
    private ImageView image_list;
    private TextView title, upload_text, clear, add_item, upload_image;
    private DatabaseHelper myDb;
    private LinearLayout check_out2;
    private ConstraintLayout check_out;
    private ConstraintLayout image_list_container, waiting, confirmed;
    private String is_also_image;
    private Bitmap imageBitmap;
    private Uri imageUri;
    private ContentValues values;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;

    private View view;
    public static final int RESULT_OK = -1;

    TextView text_address, items_selected;
    private static final int REQUEST_CODE = 121;


    private ImageView vendor_image;
    private TextView vendor_name, vendor_mobile, vendor_address, total;
    RecyclerView recyclerView_extra;

    public AddExtraItemsFragment(Context context) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Loading..");
        userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.add_extra_items_fragment, container, false);

        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
                startActivity(browserIntent);
            }
        });
        items_selected = view.findViewById(R.id.items_selected);
        text_address = view.findViewById(R.id.text_address);
        vendor_name = view.findViewById(R.id.vendor_name);
        vendor_mobile = view.findViewById(R.id.vendor_mobile);
        vendor_address = view.findViewById(R.id.vendor_address);
        vendor_image = view.findViewById(R.id.vendor_image);
        total = view.findViewById(R.id.total);
        image_list = view.findViewById(R.id.image_list);


        myDb = new DatabaseHelper(context);
        is_also_image = "NO";

        if (userSharedPrefs.getSharedPrefs("vendor_id")==null){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorsFragment(context)).commit();
        }

        add_item = view.findViewById(R.id.add_item);

        image_list_container = view.findViewById(R.id.image_list_container);
        waiting = view.findViewById(R.id.waiting);
        confirmed = view.findViewById(R.id.confirmed);

        text_address.setText(userSharedPrefs.getSharedPrefs("address"));
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (!EasyPermissions.hasPermissions(context, perms)) {
            EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
        }


        clear = view.findViewById(R.id.clear_selection);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_also_image = "NO";
                myDb.deleteWhereVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddExtraItemsFragment(context)).addToBackStack(null).commit();
            }
        });


        myDb = new DatabaseHelper(context);
        Cursor res1 = myDb.count(userSharedPrefs.getSharedPrefs("vendor_id"));
        if (res1.getCount() > 0) {
            while (res1.moveToNext()) {
                items_selected.setText(res1.getString(0));
            }
        }
        Cursor non_extra = myDb.getNonExtraItems(userSharedPrefs.getSharedPrefs("vendor_id"));
        StringBuffer cartJsonNonExtra = new StringBuffer();
        cartJsonNonExtra.append("[");
        int i = 0;
        while (non_extra.moveToNext()) {
            i++;
            cartJsonNonExtra.append(
                    "{" +
                            "\"id\":\"").append(non_extra.getString(0)).append("\"")
                    .append(", \"vpid\":\"").append(non_extra.getString(1)).append("\"")
                    .append(", \"name\":\"").append(non_extra.getString(2)).append("\"")
                    .append(", \"qty\":\"").append(non_extra.getString(3)).append("\"")
                    .append(", \"extra\":\"").append(non_extra.getString(4)).append("\"")
                    .append(", \"vendor_id\":\"").append(non_extra.getString(5)).append("\"")
                    .append(", \"extra_qty\":\"").append(non_extra.getString(6)).append("\"")
                    .append(", \"image\":\"").append(non_extra.getString(7)).append("\"")
                    .append(", \"amount\":\"").append(non_extra.getString(8)).append("\"}");

            if (non_extra.getCount() > i) {
                cartJsonNonExtra.append(",");
            }
        }
        cartJsonNonExtra.append("]");


        Cursor extra = myDb.getExtraItems(userSharedPrefs.getSharedPrefs("vendor_id"));
        StringBuffer cartJsonExtra = new StringBuffer();
        cartJsonExtra.append("[");
        int j = 0;
        while (extra.moveToNext()) {
            j++;
            cartJsonExtra.append(
                    "{" +
                            "\"id\":\"").append(extra.getString(0)).append("\"")
                    .append(", \"vpid\":\"").append(extra.getString(1)).append("\"")
                    .append(", \"name\":\"").append(extra.getString(2)).append("\"")
                    .append(", \"qty\":\"").append(extra.getString(3)).append("\"")
                    .append(", \"extra\":\"").append(extra.getString(4)).append("\"")
                    .append(", \"vendor_id\":\"").append(extra.getString(5)).append("\"")
                    .append(", \"extra_qty\":\"").append(extra.getString(6)).append("\"")
                    .append(", \"image\":\"").append(extra.getString(7)).append("\"")
                    .append(", \"amount\":\"").append(extra.getString(8)).append("\"}");

            if (extra.getCount() > j) {
                cartJsonExtra.append(",");
            }
        }
        cartJsonExtra.append("]");


        try {

            JSONArray cartNonExtraItemsJsonArray = new JSONArray(cartJsonNonExtra.toString());

            ArrayList<CartItemsModel> cart_non_extra_item_list = CartItemsModel.fromJson(cartNonExtraItemsJsonArray);
            RecyclerView recyclerView_non_extra = view.findViewById(R.id.recycler_non_extra);
            recyclerView_non_extra.clearOnScrollListeners();
            recyclerView_non_extra.setNestedScrollingEnabled(true);
            recyclerView_non_extra.setLayoutManager(new LinearLayoutManager(context));
            recyclerView_non_extra.setAdapter(new CartItemsAdapter(cart_non_extra_item_list, context));
//


            JSONArray cartExtraItemsJsonArray = new JSONArray(cartJsonExtra.toString());

            ArrayList<CartItemsModel> cart_extra_item_list = CartItemsModel.fromJson(cartExtraItemsJsonArray);
            recyclerView_extra = view.findViewById(R.id.recycler_extra);
            recyclerView_extra.clearOnScrollListeners();
            recyclerView_extra.setNestedScrollingEnabled(true);
            recyclerView_extra.setLayoutManager(new LinearLayoutManager(context));
            recyclerView_extra.setAdapter(new CartExtraItemsAdapter(cart_extra_item_list, context));
//


        } catch (Exception e) {
            Log.d("asa", "onCreateView: " + e.toString());
            e.printStackTrace();
        }


        check_out = view.findViewById(R.id.check_out);
        check_out2 = view.findViewById(R.id.check_out2);


        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_also_image.equals("YES")) {
                    Random rand = new Random();
                    int random_no = rand.nextInt(9999999);
                    String image_name = userSharedPrefs.getSharedPrefs("mobile") + random_no + ".jpg";

                    myDb.insertProductListImage(image_name, userSharedPrefs.getSharedPrefs("vendor_id"));
                }
                Cursor res = myDb.getAllItemsOfVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                if (res.getCount() > 0) {
                    Intent intent = new Intent(context, CheckOut.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No Items in the list", Toast.LENGTH_SHORT).show();
                }
            }
        });
        check_out2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_also_image.equals("YES")) {
                    Random rand = new Random();
                    int random_no = rand.nextInt(9999999);
                    String image_name = userSharedPrefs.getSharedPrefs("mobile") + random_no + ".jpg";

                    myDb.insertProductListImage(image_name, userSharedPrefs.getSharedPrefs("vendor_id"));
                }
                Cursor res = myDb.getAllItemsOfVendor(userSharedPrefs.getSharedPrefs("vendor_id"));
                if (res.getCount() > 0) {
                    Intent intent = new Intent(context, CheckOut.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No Items in the list", Toast.LENGTH_SHORT).show();
                }
            }
        });


        upload_image = view.findViewById(R.id.upload_image);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 0);
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
                            myDb.insertExtraItem(p_name, p_qty, userSharedPrefs.getSharedPrefs("vendor_id"));
//                            addProductToList(p_name, p_qty);
                            dialog.dismiss();
                            int items = Integer.parseInt(items_selected.getText().toString());
                            items++;
                            items_selected.setText(items + "");

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddExtraItemsFragment(context)).addToBackStack(null).commit();

                            // Todo: asf
                        }
                    }
                });
            }
        });

        myDb = new DatabaseHelper(context);
        Cursor res = myDb.getNonExtraItems(userSharedPrefs.getSharedPrefs("vendor_id"));


        float total_amt = 0.0f;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                int qty = Integer.parseInt(res.getString(3));
                float amount = Float.parseFloat(res.getString(8));
                total_amt = total_amt + (amount * qty);
            }
        }
        total.setText(total_amt + "");


        LoadVendorsDetails loadVendorsDetails = new LoadVendorsDetails();
        loadVendorsDetails.execute(userSharedPrefs.getSharedPrefs("vendor_id"));


        if (userSharedPrefs.getSharedPrefs("order_type") != null && userSharedPrefs.getSharedPrefs("order_type").equals("102")) {
            check_out.setVisibility(View.GONE);
            check_out2.setVisibility(View.VISIBLE);
            upload_image.setVisibility(View.VISIBLE);
        } else {
            check_out.setVisibility(View.VISIBLE);
            check_out2.setVisibility(View.GONE);
            upload_image.setVisibility(View.GONE);

        }


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {

            try {

                imageBitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), imageUri);

                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 650, 650, true);
                image_list_container.setVisibility(View.VISIBLE);
                add_item.setClickable(false);
                image_list.setImageBitmap(resized);
                is_also_image = "YES";


                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;

                File file = new File(path, "OrderList.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.

                fOut = new FileOutputStream(file);
                resized.compress(Bitmap.CompressFormat.JPEG, 75, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.requestPermissions(this, "All permissions are required to run this application", requestCode, permissions);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


    class LoadVendorsDetails extends AsyncTask<String, Void, String> {

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
                    userSharedPrefs.setSharedPrefs("order_type", vendor.getString("type"));
                    userSharedPrefs.setSharedPrefs("vendor_mobile", vendor.getString("mobile"));
                    userSharedPrefs.setSharedPrefs("business_name", vendor.getString("business_name"));
                    userSharedPrefs.setSharedPrefs("vendor_address", vendor.getString("address"));
                    userSharedPrefs.setSharedPrefs("vendor_image", vendor.getString("image"));

                    String image_path = context.getString(R.string.file_base_url) + "vendors/" + vendor.getString("image");
                    Picasso.get().load(image_path).into(vendor_image);


                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("vendor_details/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_Data = URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
