package com.miracas.espresso.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class SharedStorage {

    private static final String MY_PREFERENCES_11234234 = "MY_PREFERENCES_11234234";
    public static final String JWT = "JWT";
    private SharedPreferences sharedPreferences;

    public SharedStorage(Activity activity) {
        this.sharedPreferences = activity.getSharedPreferences(MY_PREFERENCES_11234234,Context.MODE_PRIVATE);
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void showPreferences(String label){
        Log.d("PREFERENCES",label);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("PREFERENCES map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        Log.d("PREFERENCES",label +" end");
    }

}
