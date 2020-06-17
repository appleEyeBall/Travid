package com.example.trivia.backgroundTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.model.Contact;

import java.util.ArrayList;

public class LoadContacts extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] objects) {
        ArrayList<Contact> contacts = new ArrayList<>();
        DatabaseHandler dbHandler = new DatabaseHandler((Context) objects[0]);
        contacts = dbHandler.getContacts();

        return contacts;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        ArrayList<Contact> contacts = (ArrayList<Contact>) o;
        if (contacts == null) return;

        for (Contact contact: contacts){
            Log.d("stuff", "onPostExecute: "+contact.getFirstName());
        }

    }
}
