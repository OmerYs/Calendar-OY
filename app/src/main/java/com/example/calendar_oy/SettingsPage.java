package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsPage extends AppCompatActivity {
    private LinearLayout lightModeLayout;
    private LinearLayout darkModeLayout;
    public SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.Theme_CalendarOYDark);
        } else {
            setTheme(R.style.Theme_CalendarOY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        lightModeLayout = findViewById(R.id.lightModeLayout);
        darkModeLayout = findViewById(R.id.darkModeLayout);

        lightModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setNightModeState(false);
                restartApp();
            }
        });

        darkModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setNightModeState(true);
                restartApp();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), SettingsPage.class);
        startActivity(i);
        finish();
    }
}
