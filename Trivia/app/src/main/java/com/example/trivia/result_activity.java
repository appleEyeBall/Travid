package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.trivia.data.DatabaseHandler;
import com.example.trivia.model.Contact;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class result_activity extends AppCompatActivity implements View.OnClickListener {

    EditText firstNameField;
    EditText lastNameField;
    EditText phoneNumberField;
    EditText DOBField;
    EditText faceBookField;
    Button saveBtn;
    RadioGroup gender_radio;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_activity);




    }


    @Override
    public void onClick(View view) {
        if (view == saveBtn){
            int gender_radio_id = gender_radio.getCheckedRadioButtonId();

            // set input values
            firstName = firstNameField.getText().toString();
            lastName = lastNameField.getText().toString();
            number = phoneNumberField.getText().toString();
            facebook = faceBookField.getText().toString();
            DOB = DOBField.getText().toString();
            gender = ((RadioButton) findViewById(gender_radio_id)).getText().toString().toLowerCase();

            // create intent to send back to MainActivity
            Intent return_data = new Intent();
            return_data.putExtra("firstName", firstName);
            return_data.putExtra("lastName", lastName);
            return_data.putExtra("number", number);
            return_data.putExtra("facebook", facebook);
            return_data.putExtra("DOB", DOB);
            return_data.putExtra("gender", gender);

            DatabaseHandler dbHandler = new DatabaseHandler(this.getApplicationContext());

            // make sure all fields have values
            if (!firstName.isEmpty() && !lastName.isEmpty() && !number.isEmpty() && !facebook.isEmpty() && !DOB.isEmpty()){

                // create contact to save in database
                Contact contact = new Contact(firstName, number);
                contact.setLastName(lastName);
                contact.setFacebook(facebook);
                contact.setDOB(DOB);
                contact.setGender(gender);

                // decide weather to add or update contact
                switch (agenda){
                    case "create":
                        dbHandler.addContact(contact);
                        break;
                    case "update":
                        return_data.putExtra("array_pos", array_pos);
                        dbHandler.updateContact(contact);
                        break;
                }

                // finally send result to MainActivity
                setResult(RESULT_OK, return_data);
                finish();
            }

        }
    }

}
