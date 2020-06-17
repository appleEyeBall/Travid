package com.example.trivia.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trivia.R;
import com.example.trivia.Util;
import com.example.trivia.model.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHandler extends SQLiteOpenHelper {


    public DatabaseHandler(Context context) {
        super(context, Util.DB_NAME, null, Util.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // CREATE TABLE (id, firstname, lastname, number, dob, facebook)
        String CREATE_TABLE_COMMAND = "CREATE TABLE "+Util.TABLE_NAME+ "("+
                Util.KEY_ID + " INTEGER PRIMARY KEY, " +
                Util.KEY_FIRSTNAME + " TEXT, "+
                Util.KEY_LASTNAME+" TEXT, "+
                Util.KEY_NUMBER + " TEXT, "+
                Util.KEY_DOB+" TEXT, "+
                Util.KEY_FACEBOOK + " TEXT, " +
                Util.KEY_GENDER+ " TEXT"+
                ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_COMMAND);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE = String.valueOf(R.string.drop_table);
        sqLiteDatabase.execSQL(DROP_TABLE, new String[]{Util.TABLE_NAME});

        onCreate(sqLiteDatabase);

    }

    public void addContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_FIRSTNAME, contact.getFirstName());
        values.put(Util.KEY_LASTNAME, contact.getLastName());
        values.put(Util.KEY_DOB, contact.getDOB());
        values.put(Util.KEY_FACEBOOK, contact.getFacebook());
        values.put(Util.KEY_NUMBER, contact.getNumber());
        values.put(Util.KEY_GENDER, contact.getGender());

        db.insert(Util.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Contact> getContacts(){
        ArrayList<Contact> allContacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAll = "SELECT * FROM "+Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (!cursor.moveToFirst()) {
            Log.d("stuff", "getContacts: "+" Nothing in table");
            return null;
        }

        Log.d("stuff", "getContacts: "+" something in table");

        do {
            Contact contact = new Contact(cursor.getString(1), cursor.getString(3));
            contact.setId(cursor.getInt(0));
            contact.setLastName(cursor.getString(2));
            contact.setDOB(cursor.getString(4));
            contact.setFacebook(cursor.getString(5));
            contact.setGender(cursor.getString(6));
            allContacts.add(contact);
        }
        while (cursor.moveToNext());

        return allContacts;
    }

    public void deleteContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        String id = String.valueOf(contact.getId());
        db.delete(Util.TABLE_NAME, Util.KEY_ID +"=?", new String[]{id});
        Log.d("stuff", "deleteContact: Deleted from DB");

    }
    public void updateContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_FIRSTNAME, contact.getFirstName());
        values.put(Util.KEY_LASTNAME, contact.getLastName());
        values.put(Util.KEY_DOB, contact.getDOB());
        values.put(Util.KEY_FACEBOOK, contact.getFacebook());
        values.put(Util.KEY_NUMBER, contact.getNumber());
        values.put(Util.KEY_GENDER, contact.getGender());


        String id = String.valueOf(contact.getId());
        db.update(Util.TABLE_NAME, values, Util.KEY_ID + "=?", new String[]{id});
    }

    public void logColumns(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        Log.d("stuff", "column names are: "+ Arrays.toString(columnNames));
    }
}
