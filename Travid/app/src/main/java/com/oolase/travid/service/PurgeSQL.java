package com.oolase.travid.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.oolase.travid.database.RecentBuddyDatabaseHandler;
import com.oolase.travid.utility.Util;

public class PurgeSQL extends Worker {
    String TAG = Util.LOG_TAG;
    public PurgeSQL(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: clearing database");
        RecentBuddyDatabaseHandler databaseHandler = new RecentBuddyDatabaseHandler(getApplicationContext());
        databaseHandler.clearTable();
        return Result.success();
    }
}
