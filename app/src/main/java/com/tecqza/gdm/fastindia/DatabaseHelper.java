package com.tecqza.gdm.fastindia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String db_name = "home_delivery";
    public static final String table = "cart";

    public static final String col_1 = "id";
    public static final String col_2 = "vpid";
    public static final String col_3 = "name";
    public static final String col_4 = "qty";
    public static final String col_5 = "extra";
    public static final String col_6 = "vendor_id";
    public static final String col_7 = "extra_qty";
    public static final String col_8 = "image";
    public static final String col_9 = "amount";
    public static final String col_10 = "vpvid";


    public DatabaseHelper(Context context) {
        super(context, db_name, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + table + " (id INTEGER PRIMARY KEY AUTOINCREMENT, vpid VARCHAR(11), name VARCHAR(150), qty String(11), extra VARCHAR(2), vendor_id VARCHAR(11), extra_qty VARCHAR(50), image VARCHAR(10) DEFAULT '0', amount VARCHAR(15) DEFAULT '0.0', vpvid VARCHAR(11) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    public boolean insertData(String vpid, String name, String qty, String extra, String vendor_id, String amount, String vpvid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2, vpid);
        contentValues.put(col_3, name);
        contentValues.put(col_4, qty);
        contentValues.put(col_5, extra);
        contentValues.put(col_6, vendor_id);
        contentValues.put(col_9, amount);
        contentValues.put(col_10, vpvid);

        long res = db.insert(table, null, contentValues);

        return res != -1;
    }

    public boolean insertExtraItem(String name, String extra_qty, String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_3, name);
        contentValues.put(col_5, "1");
        contentValues.put(col_6, vendor_id);
        contentValues.put(col_7, extra_qty);

        long res = db.insert(table, null, contentValues);

        return res != -1;
    }

    public boolean insertProductListImage(String name, String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_3, name);
        contentValues.put(col_5, "1");
        contentValues.put(col_6, vendor_id);
        contentValues.put(col_8, "1");

        long res = db.insert(table, null, contentValues);

        return res != -1;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table, null);
        return res;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table);
    }

    public void deleteWhereVPID(String vpid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where vpid = " + "'" + vpid + "'");
    }

    public void delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where id = " + "'" + id + "'");
    }

    public void deleteImageOfList(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where image = '1' AND vendor_id = " + "'" + vendor_id + "'");
    }

    public void deleteWhereVendor(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where vendor_id = " + "'" + vendor_id + "'");
    }



    public Cursor getExtraItems(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " where extra ='1' AND vendor_id=" + "'" + vendor_id + "'", null);
        return res;
    }

    public Cursor count(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select count(*) count from " + table + " where vendor_id = " + "'" + vendor_id + "'", null);
        return res;
    }
    public Cursor countNonExtra(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select count(*) count from " + table + " where vendor_id = " + "'" + vendor_id + "' AND   extra ='0' ", null);
        return res;
    }

    public Cursor getNonExtraItems(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " where extra ='0' AND vendor_id=" + "'" + vendor_id + "'", null);
        return res;
    }

    public Cursor getAllItemsOfVendor(String vendor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " where vendor_id=" + "'" + vendor_id + "'", null);
        return res;
    }

    public Cursor getItemOfVPId(String vpid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " where vpid = " + "'" + vpid + "'", null);
        return res;
    }

    public Cursor getItemOfVpvId(String vpvid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " where vpvid = " + "'" + vpvid + "'", null);
        return res;
    }

    public void updateQty(String vpid, String qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("qty", qty);
        db.update(table, cv, "vpid=" + vpid, null);
    }


}
