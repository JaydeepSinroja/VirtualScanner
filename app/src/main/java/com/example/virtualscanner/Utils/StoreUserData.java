package com.example.virtualscanner.Utils;

import android.content.Context;
import android.content.SharedPreferences;


public class StoreUserData {
    private SharedPreferences pref = null;
    private Context parentActivity;
    public static String APP_KEY;

    public StoreUserData(Context context) {
        parentActivity = context;
        APP_KEY = context.getPackageName().replaceAll("\\.", "_").toLowerCase();
    }

    public void setString(String key, String value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getString(key, "");

    }

    public void setDouble(String key, double value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value + "");
        editor.apply();
    }

    public Double getDouble(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        if (pref.getString(key, "").length() > 0) {
            return Double.parseDouble(pref.getString(key, ""));
        } else {
            return null;
        }
    }

    public void setBoolean(String key, boolean value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }


    public void setInt(String key, int value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }


    public boolean is_exist(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    public void clearData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }
}
