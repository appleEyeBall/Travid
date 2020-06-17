package com.example.trivia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.trivia.GlideApp;
import com.example.trivia.MainActivity;
import com.example.trivia.R;
import com.example.trivia.Util;
import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.model.Contact;
import com.example.trivia.result_activity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    ContactData contactData;
    ArrayList<Contact> contacts;
    Activity activity;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public ContactsAdapter(ArrayList<Contact> contacts, Activity activity) {
        this.contacts = contacts;
        this.activity = activity;
        this.contactData = (ContactData) activity;
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Contact contact = contacts.get(position);
        holder.contact_number.setText(contact.getNumber());
        holder.contact_name.setText(contact.getFirstName()+ " "+contact.getLastName());
        holder.delete_img.setImageResource(R.drawable.bin);
        holder.edit_img.setImageResource(R.drawable.pencil);

//        if ( contact.getGender()!=null && contact.getGender().equals("male")){
//            holder.contact_img.setImageResource(R.drawable.male_user);
//        }
//        else {
//            holder.contact_img.setImageResource(R.drawable.female_user);
//        }
        String number = "("+contact.getNumber().substring(0,3)+")-"+contact.getNumber().substring(3,6)+"-"+contact.getNumber().substring(6);
        final String suffixUrl = contact.getFirstName()+" "+contact.getLastName()+number+".jpg";
        Log.d("stuff", "onBindViewHolder: "+suffixUrl);
        storageReference = firebaseStorage.getReferenceFromUrl("gs://triviacontacts.appspot.com/"+suffixUrl);
        GlideApp.with(activity)
                .load(storageReference)
                .override(200,200)
                .circleCrop()
                .placeholder(R.drawable.male_user)
                .into(holder.contact_img);



        holder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("stuff", "onClick: delete me");
                DatabaseHandler dbHandler = new DatabaseHandler(activity);
                contacts.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                dbHandler.deleteContact(contact);
                deleteFromFireStore(contact, suffixUrl);

            }
        });


        holder.edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactData.sendData(contact, position);
            }
        });

        holder.row_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactData.showContactDetails(contact);
            }
        });



    }

    private void deleteFromFireStore(Contact contact, String suffixUrl) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://triviacontacts.appspot.com/"+suffixUrl);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contacts == null){
            return 0;
        }
        return this.contacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout row_container;
        public ImageView contact_img;
        public ImageView edit_img;
        public ImageView delete_img;
        public TextView contact_name;
        public TextView contact_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            row_container = itemView.findViewById(R.id.row_container);
            contact_img = itemView.findViewById(R.id.contact_img);
            edit_img = itemView.findViewById(R.id.edit_button);
            delete_img = itemView.findViewById(R.id.delete_button);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_number = itemView.findViewById(R.id.contact_number);
        }
    }

    public interface ContactData{
        public void sendData(Contact old_contact, int position);
        public void showContactDetails(Contact contact);
    }

}
