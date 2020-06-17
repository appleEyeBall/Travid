package com.example.trivia.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.trivia.R;
import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.model.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment implements View.OnClickListener {

    public AddContactFragment() {
        // Required empty public constructor
    }

    CompleteAdd completeAdd;
    EditText firstNameField;
    EditText lastNameField;
    EditText phoneNumberField;
    EditText DOBField;
    EditText faceBookField;
    Button saveBtn;
    RadioGroup gender_radio;
    RadioButton male;
    RadioButton female;

    String firstName;
    String lastName;
    String number;
    String facebook;
    String DOB;
    String gender;
    int old_id;
    int array_pos;

    String agenda;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        agenda = getArguments().getString("agenda");
        completeAdd = (CompleteAdd) getActivity();

        // initialize views
        firstNameField = view.findViewById(R.id.first_name);
        lastNameField = view.findViewById(R.id.last_name);
        phoneNumberField = view.findViewById(R.id.phone_number);
        DOBField = view.findViewById(R.id.DOB);
        faceBookField = view.findViewById(R.id.facebook);
        saveBtn = view.findViewById(R.id.save);
        gender_radio = view.findViewById(R.id.gender);
        male = view.findViewById(R.id.gender_male);
        female = view.findViewById(R.id.gender_female);

        saveBtn.setOnClickListener(this);


        return view;
    }

    public void populateFields(Contact old_contact, int position) {
        if (old_contact == null) return;

        old_id = old_contact.getId();
        array_pos = position;
        String old_gender = old_contact.getGender();

        firstNameField.setText(old_contact.getFirstName());
        lastNameField.setText(old_contact.getLastName());
        phoneNumberField.setText(old_contact.getNumber());
        DOBField.setText(old_contact.getDOB());
        faceBookField.setText(old_contact.getFacebook());

        switch (old_gender){
            case "male":
                male.setChecked(true);
                break;
            case "female":
                female.setChecked(true);
                break;

        }

    }

    @Override
    public void onClick(View view) {

        if (view == saveBtn){
            // set input values
            firstName = firstNameField.getText().toString();
            lastName = lastNameField.getText().toString();
            number = phoneNumberField.getText().toString();
            facebook = faceBookField.getText().toString();
            DOB = DOBField.getText().toString();
            if (male.isChecked()){
                gender = male.getText().toString().toLowerCase();
            }
            else{
                gender = female.getText().toString().toLowerCase();
            }

            DatabaseHandler dbHandler = new DatabaseHandler(getContext());

            // make sure all fields have values
            if (!firstName.isEmpty() && !lastName.isEmpty() && !number.isEmpty() && !facebook.isEmpty() && !DOB.isEmpty()){

                // create contact to save in database
                Contact contact = new Contact(firstName, number);
                contact.setLastName(lastName);
                contact.setFacebook(facebook);
                contact.setDOB(DOB);
                contact.setGender(gender);
                contact.setId(old_id);

                // decide weather to add or update contact
                switch (agenda){
                    case "create":
                        dbHandler.addContact(contact);
                        break;
                    case "update":
                        dbHandler.updateContact(contact);
                        Log.d("stuff", "onClick: UPDATED with "+contact.getFirstName());
                        break;
                }

                Log.d("stuff", "onClick: Saved!!!");

                completeAdd.getNewContact(contact, agenda, array_pos);
            }

        }

    }

    public interface CompleteAdd{
        public void getNewContact(Contact contact, String agenda, int position);
    }
}
