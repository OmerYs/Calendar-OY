package com.example.calendar_oy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setThemeState(String state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putString("ThemeState", state);
        editor.commit();
    }

    public String loadThemeState() {
        String state = mySharedPref.getString("ThemeState", "light");
        return state;
    }

    public void setNotificationsState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("Notifications", state);
        editor.commit();
    }

    public Boolean loadNotificationsState() {
        Boolean state = mySharedPref.getBoolean("Notifications", true);
        return state;
    }

    public void applyTheme(Activity activity) {
        String themeState = loadThemeState();
        if (themeState.equals("dark")) {
            activity.setTheme(R.style.Theme_CalendarOYDark);
        } else if (themeState.equals("green")) {
            activity.setTheme(R.style.Theme_CalendarOYGreen);
        } else if (themeState.equals("purple")) {
            activity.setTheme(R.style.Theme_CalendarOYPurple);
        } else {
            activity.setTheme(R.style.Theme_CalendarOY);
        }
    }
}
