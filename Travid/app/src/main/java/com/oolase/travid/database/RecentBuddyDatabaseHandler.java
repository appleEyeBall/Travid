package com.oolase.travid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oolase.travid.R;
import com.oolase.travid.utility.Util;

import java.util.ArrayList;

/*
This class was written to reduce the amount of firestore transactions.
Quick scenario: You are in a coffee shop and your bluetooth has
scanned 20 times. Would you want the app to add the bluetooth of the gentleman
sitting next to you, to firestore 20 times? No.
Store it in SQL and read it before rescanning.
NOTE: RecentBuddiesPurger will purge the sql every couple days
 */

public class RecentBuddyDatabaseHandler extends SQLiteOpenHelper {
    String TAG = Util.LOG_TAG;
    Context context;


    public RecentBuddyDatabaseHandler(Context context) {
        super(context, Util.RECENT_BUDDIES_DB, null, Util.RECENT_BUDDIES_BD_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // CREATE TABLE (id, mac)
        String CREATE_TABLE_COMMAND = "CREATE TABLE "+Util.TABLE_NAME_BUDDIES + "("+
                Util.KEY_ID + " INTEGER PRIMARY KEY, " +
                Util.KEY_MAC+ " TEXT"+
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_COMMAND);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE = String.valueOf(R.string.drop_table);
        sqLiteDatabase.execSQL(DROP_TABLE, new String[]{Util.TABLE_NAME_BUDDIES});
        onCreate(sqLiteDatabase);

    }

    public void keepBuddy(String mac){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_MAC, mac);

        db.insert(Util.TABLE_NAME_BUDDIES, null, values);
        db.close();
    }

    public boolean isBuddy(String mac){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectMac = "SELECT * FROM "+Util.TABLE_NAME_BUDDIES+ " WHERE "
                + Util.KEY_MAC + " = "+ "'"+ mac+ "'";

        Cursor cursor = db.rawQuery(selectMac, null);

        if (!cursor.moveToFirst()) {
            Log.d(TAG, "isBuddy: "+" Nothing in table");
            return false;
        }

        Log.d(TAG, "isBuddy: something in table");
        return true;
    }

    public void clearTable(){
        Log.d(TAG, "clearTable: in database, clearing table");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_NAME_BUDDIES, null, null);
        db.execSQL("delete from "+ Util.TABLE_NAME_BUDDIES);
    }
}