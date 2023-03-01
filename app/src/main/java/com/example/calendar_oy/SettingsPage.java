package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Set;

public class SettingsPage extends AppCompatActivity {
    private Switch darkModeSwitch;
    public SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()==true){
            setTheme(R.style.Theme_CalendarOYDark);
        }
        else{
            setTheme(R.style.Theme_CalendarOY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        darkModeSwitch = findViewById(R.id.darkMode_switch);

        if(sharedPref.loadNightModeState() == true){
            darkModeSwitch.setChecked(true);
        }
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                }
                else{
                    sharedPref.setNightModeState(false);
                }
                restartApp();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }

    public void restartApp(){
        Intent i = new Intent(getApplicationContext(),SettingsPage.class);
        startActivity(i);
        finish();
    }
}