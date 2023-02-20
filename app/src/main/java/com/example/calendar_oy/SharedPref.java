package com.example.calendar_oy;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String PREF_NAME = "nightModePref";
    private final String KEY_NIGHT_MODE = "nightMode";

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setNightModeState(boolean state) {
        editor.putBoolean(KEY_NIGHT_MODE, state);
        editor.apply();
    }

    public boolean loadNightModeState() {
        return sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
    }

    public void saveNightModeState(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }
}

