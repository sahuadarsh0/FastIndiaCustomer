package com.tecqza.gdm.fastindia;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddOrderItems extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button add_item;
    LinearLayout item_container_linearlayout;
    Context context;
    SharedPrefs userSharedPrefs;
    ImageView icon, upload_image;
    TextView title, upload_text, clear;
    DatabaseHelper myDb;
    Button check_out;
    Intent intent;
    ImageView image_list;
    ConstraintLayout image_list_container;
    String list_type;
    Bitmap imageBitmap;
    Uri imageUri;
    ContentValues values;
    private static final int REQUEST_CODE = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_items);
        context = this;
        userSharedPrefs = new SharedPrefs(context, "USER");
        myDb = new DatabaseHelper(context);
        list_type = "ITEMS";

        add_item = findViewById(R.id.add_item);
        item_container_linearlayout = findViewById(R.id.item_container_linearlayout);

        image_list_container = findViewById(R.id.image_list_container);

        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
        }

        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        upload_text = findViewById(R.id.upload_text);


        switch (userSharedPrefs.getSharedPrefs("order_type")) {
            case "101":
                title.setText("Grocery");
                break;
            case "102":
                title.setText("Medicine");
                break;
            case "103":
                title.setText("Dairy");
                break;
        }

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_type.equals("IMAGE")) {
                    image_list_container.setVisibility(View.GONE);
                    list_type = "ITEMS";
                    add_item.setClickable(true);
                } else {
                    LinearLayout linearLayout = findViewById(R.id.item_container_linearlayout);
                    linearLayout.removeAllViews();
                }


            }
        });


//        Cursor res = myDb.getItemsOfOrderType(userSharedPrefs.getSharedPrefs("order_type"));
//        if (res.getCount() > 0) {
//            while (res.moveToNext()) {
//                addProductToList(res.getString(1), res.getString(2));
//            }
//        }


        check_out = findViewById(R.id.check_out);
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Cursor res = myDb.getItemsOfOrderType(userSharedPrefs.getSharedPrefs("order_type"));
//        if(res.getCount()>0){
//                if (list_type.equals("ITEMS") && res.getCount() == 0) {
//                    Toast.makeText(AddOrderItems.this, "No Items in the list", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent i = new Intent(context, CheckOut.class);
//                    i.putExtra("list_type", list_type);
//                    startActivity(i);
//                }
            }
        });

        image_list = findViewById(R.id.image_list);

        upload_image = findViewById(R.id.upload_image);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
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
                Button add_button, cancel_button;

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
                            addProductToList(p_name, p_qty);
//                            myDb.insertData(p_name, p_qty, userSharedPrefs.getSharedPrefs("order_type"));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            try {

                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);

                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 650, 650, true);
                image_list_container.setVisibility(View.VISIBLE);
                add_item.setClickable(false);
                image_list.setImageBitmap(resized);
                list_type = "IMAGE";


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

    public void addProductToList(String name, String qty) {
        LinearLayout item_container = new LinearLayout(context);
        item_container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        item_container.setOrientation(LinearLayout.HORIZONTAL);
        item_container.setPadding(5, 10, 0, 10);

        TextView item = new TextView(context);
        LinearLayout.LayoutParams item_layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        item.setLayoutParams(item_layout);
        item.setTextSize(16f);
        item.setTextColor(getResources().getColor(R.color.blue));

        item.setText(name);

        TextView item_qty = new TextView(context);
        LinearLayout.LayoutParams item_qty_layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 4f);
        item_qty.setLayoutParams(item_qty_layout);
        item_qty.setTextSize(16f);
        item_qty.setTextColor(getResources().getColor(R.color.blue));

        item_qty.setText(qty);


        item_container.addView(item);
        item_container.addView(item_qty);

        item_container_linearlayout.addView(item_container);
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
        new AppSettingsDialog.Builder(this).build().show();
    }
}
