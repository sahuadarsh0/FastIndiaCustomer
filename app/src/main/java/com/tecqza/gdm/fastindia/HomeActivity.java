package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.PaymentResultListener;

public class HomeActivity extends AppCompatActivity implements PaymentResultListener {

    BottomNavigationView bottomNavigationView;
    Context context;
    Bundle bundle;
    DatabaseHelper myDb;
    SharedPrefs userSharedPrefs, notify;
    String cart_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        context = this;
        bundle = getIntent().getExtras();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userSharedPrefs = new SharedPrefs(context, "USER");
        notify = new SharedPrefs(context, "NOTIFY");


        if (userSharedPrefs.getSharedPrefs("city_id") == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment(context, bottomNavigationView)).addToBackStack(null).commit();
        }

        myDb = new DatabaseHelper(context);
        Cursor res = myDb.count(userSharedPrefs.getSharedPrefs("vendor_id"));
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                cart_items = res.getString(0);
            }
        }


        String open = bundle.getString("open", null);
        if (bundle != null && open != null) {

            switch (bundle.getString("open", null)) {
                case "ORDER_TYPE":
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderTypeFragment(context, bottomNavigationView)).addToBackStack(null).commit();

                    break;
                case "Notification":
                    bottomNavigationView.setSelectedItemId(R.id.setting);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationsFragment(context)).addToBackStack(null).commit();

                    break;
                case "ORDER_PLACED":
                    bottomNavigationView.setSelectedItemId(R.id.order);
                    String order_id = bundle.getString("order_id");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderFragment(context, order_id)).addToBackStack(null).commit();
                    break;

                default:
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderTypeFragment(context, bottomNavigationView)).addToBackStack(null).addToBackStack(null).commit();
                    break;
            }
        } else {

            if (userSharedPrefs.getSharedPrefs("city_id") != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new VendorsFragment(context)).addToBackStack(null).commit();

            } else {
                bottomNavigationView.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderTypeFragment(context, bottomNavigationView)).addToBackStack(null).addToBackStack(null).commit();
            }
        }


        String fragment = getIntent().getStringExtra("fragment");
        String orderId = getIntent().getStringExtra("extra");


        if (fragment != null && fragment.equals("ORDER_DETAIL")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderFragment(context, orderId)).addToBackStack(null).commit();
        } else if (fragment != null && fragment.equals("NOTIFY")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationsFragment(context)).addToBackStack(null).commit();
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new OrderTypeFragment(context, bottomNavigationView);

                        break;
                    case R.id.setting:
                        selectedFragment = new SettingsFragment(context, bottomNavigationView);
                        break;

                    case R.id.order:
                        String order_id = userSharedPrefs.getSharedPrefs("order_id");
                        if (order_id != null)
                            selectedFragment = new OrderFragment(context, order_id);
                        else
                            selectedFragment = new OrderHistoryFragment(context);

                        break;
                    case R.id.cart:
                        if (userSharedPrefs.getSharedPrefs("vendor_id") != null)
                            selectedFragment = new AddExtraItemsFragment(context);
                        else
                            Toast.makeText(context, "No Item in Cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.fav:
                        selectedFragment = new FavouriteFragment(context);

                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).addToBackStack(null).commit();
                } else {
//                    Toast.makeText(context, "Fragment Not Defined", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });




    }


    @Override
    public void onPaymentSuccess(String s) {
//        RecordPayment recordPayment = new RecordPayment();
//        recordPayment.execute(s,order_id);


    }


    @Override
    public void onPaymentError(int i, String s) {

        Log.d("asa", "onPaymentError: " + s);
    }

}
