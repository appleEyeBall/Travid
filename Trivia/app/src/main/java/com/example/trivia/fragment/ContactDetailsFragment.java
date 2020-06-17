package com.example.trivia.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.GlideApp;
import com.example.trivia.R;
import com.example.trivia.Util;
import com.example.trivia.model.Contact;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailsFragment extends Fragment {

    public ContactDetailsFragment() {
        // Required empty public constructor
    }

    ImageView profileImage;
    TextView nameField;
    TextView facebookName;
    TextView DOBField;
    TextView numberField;
    int genderRes;
    String image_key;
    FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        firebaseStorage = FirebaseStorage.getInstance();

        profileImage = view.findViewById(R.id.contact_img);
        nameField = view.findViewById(R.id.contact_name);
        facebookName = view.findViewById(R.id.fb_name);
        DOBField = view.findViewById(R.id.birthday);
        numberField = view.findViewById(R.id.contact_number);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = Intent.createChooser(cameraIntent, "Pick an image");
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { galleryIntent });
                getActivity().startActivityForResult(chooser, Util.GET_IMAGE_REQUEST);


            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String name = nameField.getText().toString();
        String number = numberField.getText().toString();
        String facebook = facebookName.getText().toString();
        String birthday = DOBField.getText().toString();

        outState.putString("name", name);
        outState.putString("number", number);
        outState.putString("facebook", facebook);
        outState.putString("birthday", birthday);
        outState.putInt("imgRes", genderRes);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) return;

        genderRes = savedInstanceState.getInt("imgRes");
        profileImage.setImageResource(genderRes);
        nameField.setText(savedInstanceState.getString("name"));
        facebookName.setText(savedInstanceState.getString("facebook"));
        DOBField.setText(savedInstanceState.getString("birthday"));
        numberField.setText(savedInstanceState.getString("number"));


    }

    public void populateUI(Contact contact){
        String name = contact.getFirstName()+" "+contact.getLastName();
        String number = "("+contact.getNumber().substring(0,3)+")-"+contact.getNumber().substring(3,6)+"-"+contact.getNumber().substring(6);

        nameField.setText(name);
        facebookName.setText(contact.getFacebook());
        DOBField.setText(contact.getDOB());
        numberField.setText(number);

        image_key = nameField.getText().toString()+numberField.getText().toString();

//        if (contact.getGender().equals("male")){
//            profileImage.setImageResource(R.drawable.male_user);
//            genderRes = R.drawable.male_user;
//        }
//        else{
//            profileImage.setImageResource(R.drawable.female_user);
//            genderRes = R.drawable.female_user;
//        }

        StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://triviacontacts.appspot.com/"+image_key+".jpg");
        GlideApp.with(getActivity())
                .load(storageReference)
                .override(600,600)
                .circleCrop()
                .placeholder(R.drawable.male_user)
                .into(profileImage);

        Log.d("stuff", "populateUI: "+image_key);

    }

    public void setDisplayImage(Bitmap bitmap){
        profileImage.setImageBitmap(bitmap);
        Log.d("stuff", "setdisplayImage: here");
        uploadToFireStore(bitmap);
    }

    public void uploadToFireStore(Bitmap bitmap){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap = scaleDown(bitmap);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "profPic", null);
        Uri file = Uri.parse(path);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageReference.child(image_key+".jpg");

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();
                Log.d("stuff", "onSuccess: Success");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("stuff", "onFailure: Failed "+e.toString());

            }
        });

    }

    private Bitmap scaleDown(Bitmap realImage) {
        int width = realImage.getWidth();
        int height = realImage.getHeight();
        while (width > 360){
            width = width/2;
            height = height/2;
        }

        return Bitmap.createScaledBitmap(realImage, width, height, false);


    }
}
