package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;

public class Utility {
    DatabaseHelper myDb;
    SharedPrefs userSharePrefs;
    public Utility(Context context){
        myDb = new DatabaseHelper(context);
        this.userSharePrefs = new SharedPrefs(context, "USER");
    }

    public float getCartItemsTotalAmount(){
        Cursor res = myDb.getNonExtraItems(userSharePrefs.getSharedPrefs("vendor_id"));

        float total_amt = 0.0f;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                int qty = Integer.parseInt(res.getString(3));
                float amount = Float.parseFloat(res.getString(8));
                total_amt = total_amt + (amount * qty);
            }
        }
        return  total_amt;
    }
}
