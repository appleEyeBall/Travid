package com.oolase.travid.utility;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.oolase.travid.activity.NewsActivity;
import com.oolase.travid.dialog.UserBluetoothAddressDialog;

import java.util.HashMap;
import java.util.Map;

public class UserRegisteration implements UserBluetoothAddressDialog.OnAddressEntered {

    NewsActivity newsActivity;
    String TAG = Util.LOG_TAG;
    UserRegisteration thisUserRegisteration;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isLocationPermitted;

    public UserRegisteration(NewsActivity newsActivity) {
        this.newsActivity = newsActivity;
        this.thisUserRegisteration = this;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = newsActivity.getSharedPreferences(Util.USER_MAC, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //check fine location permission
        isLocationPermitted = newsActivity.permissionRequester(Manifest.permission.ACCESS_FINE_LOCATION, Util.LOCATION_REQUEST);
    }

    // after the user has inputted his MAC address
    @Override
    public void getAddress(final String userMACAdress) {
        if (userMACAdress.isEmpty()){
            registerUser();
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String firebaseIDToken = task.getResult().getToken();
                Log.d(TAG, "onComplete: Gotten MAC address");
                verifyUser(userMACAdress, firebaseIDToken);
            }
        });
    }

    /* class's entry point */
    public void registerUser(){
        if (!isLocationPermitted){
            Log.d(TAG, "registerUser: not permitted");
            return;
        }
        Log.d(TAG, "registerUser: permitted");
        // show bluetooth dialog
        UserBluetoothAddressDialog userBluetoothAddressDialog = new UserBluetoothAddressDialog(thisUserRegisteration);
        userBluetoothAddressDialog.show(newsActivity.getSupportFragmentManager(), "getMac");

        /* response to be returnd to "getAddress(...)" */
    }

    private void verifyUser(final String bluetoothMAC, final String firebaseIDToken){
        // check if the user exists in database
        Log.d(TAG, "verifyUser: Got here");
        DocumentReference existing_user = db.collection("users").document(bluetoothMAC.toUpperCase());
        existing_user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document!= null && !document.exists()){
                    // user is NOT in firestore, add user
                    Log.d(TAG, "onComplete: This is a new user");
                    addUserToFirestore(bluetoothMAC, firebaseIDToken);
                }
                else if (document!=null && document.exists()){
                    // user is in firestore, update user's token
                    Log.d(TAG, "onComplete: This is NOT a new user");
                    updateUserToken(bluetoothMAC, firebaseIDToken);
                }



            }
        });


    }

    // new user
    private void addUserToFirestore(final String bluetoothMAC, String firebaseIDToken){
        Map<String, Object> user = new HashMap<>();
        user.put("token", firebaseIDToken);
        Log.d(TAG, "addUserToDB: Adding user");

        db.collection("users").document(bluetoothMAC.toUpperCase()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editor.putString(Util.USER_MAC, bluetoothMAC.toUpperCase()).commit();
                newsActivity.startBluetoothSearch();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ADDING USER TO DATABASE FAIL");
                SignOut.signOut(newsActivity);
            }
        });

    }

    // Existing user
    private void updateUserToken(final String bluetoothMac, String firebaseIdToken){
        DocumentReference old_user = db.collection("users").document(bluetoothMac.toUpperCase());
        old_user.update("token", firebaseIdToken).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: UPDATING USER IN DATABASE FAIL");
                SignOut.signOut(newsActivity);

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editor.putString(Util.USER_MAC, bluetoothMac.toUpperCase()).commit();
                newsActivity.startBluetoothSearch();
            }
        });
    }


}
