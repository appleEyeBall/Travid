package com.oolase.travid.utility;

import android.Manifest;

public class Util {
    public static String LOG_TAG = "LOG_TAG";

    public static int SIGN_IN_REQUEST = 1031;
    public static int LOCATION_REQUEST = 221;
    public static int BACKGROUND_LOCATION_REQ = 222;
    public static int RETURN_FROM_SETTINGS_PERMISSION_REQ = 1024;
    public static String USER_MAC = "userMac";
    public static String LOCATE_BUDDIES_WORK = "locateBuddies";

    // recent buddies sqlite database stuff
    public static String RECENT_BUDDIES_DB = "recent_buddies";
    public static String TABLE_NAME_BUDDIES = "recentbuddies";
    public static int RECENT_BUDDIES_BD_VERSION = 1;
    public static String KEY_ID = "id";
    public static String KEY_MAC = "mac";

    // notification stuff
    public static int LOCATING_NOTIFICATION_ID = 12;
    public static String CHANNEL_ID = "locatingBuddyChannel";
    public static String LOCATING_NOTIFICATION_TITLE = "Bluetooth search is on";

    public static long LOCATE_BUDDIES_WORK_FREQ = 180000;

    public static String PURGE_WORK = "purgeSQL";
    public static int PURGE_SQL_DAYS = 2;

}
