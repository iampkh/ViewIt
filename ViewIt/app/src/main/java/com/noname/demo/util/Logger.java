package com.noname.demo.util;

import android.util.Log;

public class Logger {
    private static String TAG = "ViewIt";
    public static boolean DEBUG = true;
    public static void dLog(String log){
        if(DEBUG) {
            Log.d(TAG, "Logger:"+log);
        }
    }
    public static void eLog(String log){
        if(DEBUG) {
            Log.e(TAG, "Logger:"+log);
        }
    }
}
