package com.example.trivia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.adapter.ContactsAdapter;
import com.example.trivia.backgroundTasks.LoadContacts;
import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.fragment.AddContactFragment;
import com.example.trivia.fragment.ContactDetailsFragment;
import com.example.trivia.fragment.ContactListFragment;
import com.example.trivia.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ContactListFragment.ContactData, ContactsAdapter.ContactData, AddContactFragment.CompleteAdd{

    private final int REQUEST_CODE = 231;
    FragmentManager manager;
    int screenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenSize = this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        manager = this.getSupportFragmentManager();
        Fragment listFragment = manager.findFragmentById(R.id.contact_list_container);

        if (listFragment == null){
            listFragment = new ContactListFragment();
            manager.beginTransaction().add(R.id.contact_list_container, listFragment).commit();
        }



    }


//    Used by both ContactsAdapter and ContactListFragment
    @Override
    public void sendData(Contact old_contact, int position) {
        Bundle bundle = new Bundle();
        if(old_contact == null){
            bundle.putString("agenda", "create");
        }
        else{
            bundle.putString("agenda", "update");
        }

        AddContactFragment addContactFragment = new AddContactFragment();
        addContactFragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.contact_list_container, addContactFragment).addToBackStack("addFragment").commit();
        manager.executePendingTransactions();

        addContactFragment.populateFields(old_contact, position);

    }

    @Override
    public void showContactDetails(Contact contact) {
        ContactDetailsFragment contactDetailsFragment;
        if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE){
             contactDetailsFragment = (ContactDetailsFragment) manager.findFragmentById(R.id.contact_details_container);
             if (contactDetailsFragment == null){
                 contactDetailsFragment = new ContactDetailsFragment();
                 manager.beginTransaction().add(R.id.contact_details_container, contactDetailsFragment).commit();
                 manager.executePendingTransactions();
             }
        }
        else{
            contactDetailsFragment = new ContactDetailsFragment();
            manager.beginTransaction().replace(R.id.contact_list_container, contactDetailsFragment).addToBackStack("detailsFrag").commit();
            manager.executePendingTransactions();
        }

        contactDetailsFragment.populateUI(contact);
    }

    @Override
    public void getNewContact(Contact contact, String agenda, int position) {
        manager.popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.GET_IMAGE_REQUEST && resultCode == RESULT_OK){
            ContactDetailsFragment contactDetailsFragment;
            if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE){
                contactDetailsFragment = (ContactDetailsFragment) manager.findFragmentById(R.id.contact_details_container);
            }
            else{
                contactDetailsFragment = (ContactDetailsFragment) manager.findFragmentById(R.id.contact_list_container);
            }
            Log.d("stuff", "onActivityResult: here");

            if (data.hasExtra("data")){
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                contactDetailsFragment.setDisplayImage(imageBitmap);
            }
            else{

                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    contactDetailsFragment.setDisplayImage(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
