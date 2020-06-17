package com.oolase.travid.utility;

import android.content.Context;

import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.oolase.travid.activity.NewsActivity;


public class SignOut{
    // pass "getApplicationContext" to avoid activity recreation
    public static void signOut(Context context){
        WorkManager.getInstance(context).cancelAllWork();
        FirebaseAuth.getInstance().signOut();
        if (context instanceof NewsActivity){
            ((NewsActivity) context).recreate();
        }
    }

}
