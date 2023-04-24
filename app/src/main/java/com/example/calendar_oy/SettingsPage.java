package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class SettingsPage extends AppCompatActivity {
    private LinearLayout lightModeLayout;
    private LinearLayout darkModeLayout;
    private LinearLayout greenModeLayout;
    private LinearLayout purpleModeLayout;
    public SharedPref sharedPref;
    private Switch notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        sharedPref.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        lightModeLayout = findViewById(R.id.lightModeLayout);
        darkModeLayout = findViewById(R.id.darkModeLayout);
        greenModeLayout = findViewById(R.id.greenModeLayout);
        purpleModeLayout = findViewById(R.id.purpleModeLayout);

        lightModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setThemeState("light");
                restartApp();
            }
        });

        darkModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setThemeState("dark");
                restartApp();
            }
        });

        greenModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setThemeState("green");
                restartApp();
            }
        });

        purpleModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setThemeState("purple");
                restartApp();
            }
        });


        notificationsSwitch = findViewById(R.id.notifications_switch);
        notificationsSwitch.setChecked(sharedPref.loadNotificationsState());
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPref.setNotificationsState(isChecked);
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
