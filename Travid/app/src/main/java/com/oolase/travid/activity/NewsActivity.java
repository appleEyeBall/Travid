package com.oolase.travid.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oolase.travid.R;
import com.oolase.travid.dialog.BackgroundBluetoothPermissionDialog;
import com.oolase.travid.dialog.RevokedPermissionDialog;
import com.oolase.travid.service.LocateBuddies;
import com.oolase.travid.service.PurgeSQL;
import com.oolase.travid.utility.SignOut;
import com.oolase.travid.utility.UserRegisteration;
import com.oolase.travid.utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/* AKA Mainactivity */
public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser currentUser;
    private String TAG = Util.LOG_TAG;

    private ImageView profilePic;
    private TextView profileName;
    DrawerLayout drawer;
    NavigationView navigationView;
    NavController navController;
    Switch buddyLocateSwitch;
    TextView signOut;
    UserRegisteration userRegisteration;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Views initialization
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        profilePic = navigationView.getHeaderView(0).findViewById(R.id.profilePic);
        profileName = navigationView.getHeaderView(0).findViewById(R.id.profileName);
        buddyLocateSwitch = navigationView.getHeaderView(0).findViewById(R.id.buddy_locate_switch);
        buddyLocateSwitch.setOnClickListener(this);

        navigationView.bringToFront();

        sharedPreferences = getSharedPreferences(Util.USER_MAC, Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // sign in flow
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            userSignIn();
        }

        else{
            updateUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocatingBuddies()){
            buddyLocateSwitch.setChecked(true);
        }
        else{
            buddyLocateSwitch.setChecked(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sharedPreferences.getString(Util.USER_MAC, null) == null){
            SignOut.signOut(this.getApplicationContext());
        }
        Log.d(TAG, "onStop: onStop");
    }

    @Override
    public void onClick(View view) {
        if (view == buddyLocateSwitch){
            Log.d(TAG, "onClick: switch pressed");
            if (!buddyLocateSwitch.isChecked()){
                stopBluetoothSearch();
                Log.d(TAG, "onClick: turning off");
                Toast.makeText(getApplicationContext(), "Turned off passive bluetooth search", Toast.LENGTH_LONG);
                buddyLocateSwitch.setChecked(false);
            }
            else{
                startBluetoothSearch();
                Log.d(TAG, "onClick: turning on");
                Toast.makeText(getApplicationContext(), "Turned on passive bluetooth search", Toast.LENGTH_LONG);
                buddyLocateSwitch.setChecked(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: hERE");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.SIGN_IN_REQUEST){
            if (resultCode == RESULT_OK){
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                registerUserInFirestore();
                updateUI();
                Log.d(TAG, "onActivityResult: Signed in");
            }
            else{
                userSignIn();
            }
        }
        else if (requestCode == Util.RETURN_FROM_SETTINGS_PERMISSION_REQ){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                startBluetoothSearch();
            }
            else{
                SignOut.signOut(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RevokedPermissionDialog revokedPermissionDialog = new RevokedPermissionDialog();
        if (requestCode == Util.LOCATION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // retry firestore registration
                registerUserInFirestore();
            } else {
                // show dialog explaining that the user can't use the app without the permission and cancel activity there
                revokedPermissionDialog.show(getSupportFragmentManager(), "revokedLocationPermission");
            }
            return;
        }
        else if (requestCode == Util.BACKGROUND_LOCATION_REQ){
            // guide user to settings, to grant permission
            BackgroundBluetoothPermissionDialog backgroundBluetoothPermissionDialog = new BackgroundBluetoothPermissionDialog();
            backgroundBluetoothPermissionDialog.show(getSupportFragmentManager(), "backgroundLocationPermission");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() == getResources().getString(R.string.sign_out)){
            // sign out and restart activity
            FirebaseAuth.getInstance().signOut();
            recreate();

        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }






    public void updateUI(){
        String name = "";
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().equals("")){
            Log.d(TAG, "updateUI: displayName is not null");
            name = currentUser.getDisplayName();
        }
        else if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().equals("")){
            Log.d(TAG, "updateUI: phone number is not null");
            name = currentUser.getPhoneNumber();
        }

        Log.d(TAG, "updateUI: "+currentUser.getDisplayName());
        // update user info in navigation drawer
        profileName.setText(name);

        Glide.with(profilePic)
                .load(currentUser.getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(profilePic);

        if (isLocatingBuddies()){
            Log.d(TAG, "updateUI: Is locating buddies");
            buddyLocateSwitch.setChecked(true);
        }

        else{
            Log.d(TAG, "updateUI: wasn't locating buddies");
        }

    }

    private void registerUserInFirestore(){
        userRegisteration = new UserRegisteration(this);
        userRegisteration.registerUser();
    }

    public boolean permissionRequester(String permission, int REQ_CODE){
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_CODE);
            return false;
        }
    }

    private void userSignIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                Util.SIGN_IN_REQUEST);

    }


    public void startBluetoothSearch(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.permissionRequester(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Util.BACKGROUND_LOCATION_REQ);
        }

        // for sql-purge
        PeriodicWorkRequest purgeSQL = new PeriodicWorkRequest
                .Builder(PurgeSQL.class, Util.PURGE_SQL_DAYS, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(Util.PURGE_WORK, ExistingPeriodicWorkPolicy.REPLACE, purgeSQL);


        // for bluetooth search (LocateBuddies)
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        OneTimeWorkRequest locateBuddies = new OneTimeWorkRequest
                .Builder(LocateBuddies.class)
                .setConstraints(constraints).build();

        WorkManager.getInstance(this)
                .enqueueUniqueWork(Util.LOCATE_BUDDIES_WORK, ExistingWorkPolicy.REPLACE, locateBuddies);

        Log.d(TAG, "onSuccess: SHAREDPREF IS"+ sharedPreferences.getString(Util.USER_MAC, null));

        // after work has been scheduled, set the switch appropriately
        if (isLocatingBuddies()){
            buddyLocateSwitch.setChecked(true);
            Log.d(TAG, "isRegistrationCompleted: switch activated");
        }

    }

    public void stopBluetoothSearch(){
        WorkManager.getInstance(this).cancelUniqueWork(Util.LOCATE_BUDDIES_WORK);
        WorkManager.getInstance(this).cancelUniqueWork(Util.PURGE_WORK);
        buddyLocateSwitch.setChecked(false);
    }

    /*  Check if the "LocateBuddies" work is processing */
    private boolean isLocatingBuddies(){
        ListenableFuture data = WorkManager.getInstance(getApplicationContext()).getWorkInfosForUniqueWork(Util.LOCATE_BUDDIES_WORK);
        WorkInfo.State state = null;
        try {
            ArrayList infos = (ArrayList) data.get();
            if (infos.isEmpty()) return false;
            WorkInfo info = (WorkInfo) infos.get(0);
            state = info.getState();
            Log.d(TAG, "buddyLocateStatus: "+info.getState());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.BLOCKED);

    }
}
