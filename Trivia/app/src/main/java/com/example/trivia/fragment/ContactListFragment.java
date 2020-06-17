package com.example.trivia.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trivia.MainActivity;
import com.example.trivia.R;
import com.example.trivia.adapter.ContactsAdapter;
import com.example.trivia.backgroundTasks.LoadContacts;
import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.model.Contact;
import com.example.trivia.result_activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment implements View.OnClickListener {

    public ContactListFragment() {
        // Required empty public constructor
    }

    ContactData contactData;

    FloatingActionButton fab;
    RecyclerView contacts_recycler;
    private final int REQUEST_CODE = 231;
    ArrayList<Contact> contacts;
    ContactsAdapter contactsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        contacts_recycler = view.findViewById(R.id.contact_recycler);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        Log.d("stuff", "onCreateView: done");

        contactData = (ContactData) getActivity();

        updateUI();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("stuff", "onAttach: done");

    }




    private void updateUI(){
        Log.d("stuff", "updateUI: "+"Loading contacts");
//        get all contact images

        try {
            Log.d("stuff", "updateUI: "+"Loading contacts");
            contacts = (ArrayList<Contact>) new LoadContacts().execute(new Context[]{getContext()}).get();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d("stuff", "updateUI: Load contacts fail "+String.valueOf(e));
        }

        if (contacts == null) contacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(contacts, getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        contacts_recycler.setLayoutManager(llm);

        contacts_recycler.setAdapter(contactsAdapter);

    }

    public void clearDB(){
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        ArrayList<Contact> conts = dbHandler.getContacts();
        if (conts == null) return;
        for (Contact cont: conts){
            dbHandler.deleteContact(cont);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == fab){
            contactData.sendData(null, -1);

        }

    }

    public interface ContactData{
        public void sendData(Contact old_contact, int position);
    }

    public void updateContactList(Contact contact, String agenda, int position){
        if (agenda.equals("create")){
            contacts.add(contact);
            contactsAdapter.notifyItemInserted(contacts.size()-1);
        }
        else if (agenda.equals("update")){
            contactsAdapter.notifyItemChanged(position);
        }
    }
}
