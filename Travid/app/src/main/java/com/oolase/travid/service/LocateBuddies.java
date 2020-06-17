package com.oolase.travid.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oolase.travid.database.RecentBuddyDatabaseHandler;
import com.oolase.travid.utility.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
This class Locates devices around, using bluetooth, every 3 minutes.
It stores to firestore, the current timestamp, and address of the
 users that are withing a reasonable range (using RSSI).
 Before storing in firestore, we check if we have 'seen'
 the device today (Using SQL).
 If we have, we don't bother to store in firestore.
 NOTE: Please do not confuse firestore with sql.
  I try not to use the word "database" because of ambiguity
 */

public class LocateBuddies extends Worker{
    String TAG = Util.LOG_TAG;

    public LocateBuddies(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "LocateBuddies doWork: Sleeping..... ");
        try {
            Thread.sleep(Util.LOCATE_BUDDIES_WORK_FREQ);
        } catch (InterruptedException e) {
            Log.e(TAG, "LocateBuddies doWork: Thread error"+ e.toString());
            e.printStackTrace();
        }
        Log.d(TAG, "LocateBuddies doWork: woken up");
//        setForegroundAsync(createForegroundInfo("progress"));
        ScanDevices scanDevices = new ScanDevices(getApplicationContext(), bluetoothScanReciever);
        scanDevices.run();

        return Result.success();
    }

    // BroadcastReceiver: if bluetooth device is found
    private BroadcastReceiver bluetoothScanReciever =  new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: in reciever");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // get name, MAC, RSSI and current timestamp
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                long dateStamp = System.currentTimeMillis();
                Log.d(TAG, "onReceive: "+ deviceName+" "+deviceHardwareAddress+" "+rssi);

                GetAddressAndStoreInFireStore getAddressAndStoreInFireStore = new GetAddressAndStoreInFireStore(context, bluetoothScanReciever, dateStamp, deviceHardwareAddress, rssi);
                getAddressAndStoreInFireStore.run();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                // discovery started, restart work
                WorkPersistor.restart(context);
            }
        }
    };

}


class ScanDevices{
    Context context;
    String TAG = Util.LOG_TAG;
    BroadcastReceiver bluetoothScanReciever;

    ScanDevices(Context context, BroadcastReceiver bluetoothScanReciever) {
        this.context = context;
        this.bluetoothScanReciever = bluetoothScanReciever;
    }

    void run() {
        // find bluetooth devices
        BluetoothAdapter adapter;
        adapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(bluetoothScanReciever, filter);

        if (!adapter.isEnabled()){
            Log.d(TAG, "run: adapter wan't enabled");
            adapter.enable();
        }

        boolean isDisc = adapter.startDiscovery();
        Log.d(TAG, "storeInFirestore: isDiscoverey started is: "+isDisc);
    }

}

class GetAddressAndStoreInFireStore{
    Context context;
    String otherBuddyBluetoothAddress;
    String TAG = Util.LOG_TAG;
    long datestamp;
    private FusedLocationProviderClient fusedLocationClient;
    Geocoder geocoder;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences preferences;
    String userMac;
    int rssi;
    BroadcastReceiver receiver;
    RecentBuddyDatabaseHandler buddyDatabase;

    GetAddressAndStoreInFireStore(Context context, BroadcastReceiver receiver, long datestamp, String otherBuddyBluetoothAddress, int rssi) {
        this.context = context;
        this.datestamp = datestamp;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.geocoder = new Geocoder(context, Locale.getDefault());
        this.otherBuddyBluetoothAddress = otherBuddyBluetoothAddress;
        this.preferences = context.getSharedPreferences(Util.USER_MAC, Context.MODE_PRIVATE);
        this.userMac = preferences.getString(Util.USER_MAC, null);
        this.rssi = rssi;
        this.receiver = receiver;
        buddyDatabase = new RecentBuddyDatabaseHandler(context);
    }

    void run() {
        Log.d(TAG, "run: getting address and all");
        // Get the user's coarse address
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();

                    String address = null;
                    try {
                        address = geocoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0);
                    } catch (IOException e) {
                        Log.e(TAG, "onSuccess: error"+e.toString());
                        e.printStackTrace();
                    }

                    // store in firestore
                    if (rssi >= -85 && !buddyDatabase.isBuddy(otherBuddyBluetoothAddress)){
                        Map<String, Object> otherBuddy = new HashMap<>();
                        otherBuddy.put("address", address);
                        otherBuddy.put("datestamp", datestamp);
                        db.collection("users").document(userMac)
                                .collection("buddies").document(otherBuddyBluetoothAddress).set(otherBuddy);
                        // store in cloudbase
                        buddyDatabase.keepBuddy(otherBuddyBluetoothAddress);
                        Log.d(TAG, "onSuccess: added to firestore");
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get location");

            }
        });
    }
}

class WorkPersistor {
    private static String TAG = Util.LOG_TAG;
    static void restart(Context context){
        Log.d(TAG, "restart: restarting");

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        OneTimeWorkRequest locateBuddies = new OneTimeWorkRequest
                .Builder(LocateBuddies.class)
                .setConstraints(constraints).build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(Util.LOCATE_BUDDIES_WORK, ExistingWorkPolicy.APPEND, locateBuddies);

    }
}
