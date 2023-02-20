package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Set;

public class SettingsPage extends AppCompatActivity {

    private SharedPref sharedPref;
    private SwitchCompat nightModeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        // Set the theme based on saved preference
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.Theme_CalendarOYDark);
        } else {
            setTheme(R.style.Theme_CalendarOY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Find the night mode toggle view in the layout
        nightModeToggle = findViewById(R.id.darkMode_switch);
        // Set the initial state of the toggle based on saved preference
        nightModeToggle.setChecked(sharedPref.loadNightModeState());

        // Add the OnCheckedChangeListener to the night mode toggle
        nightModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state to shared preference
                sharedPref.saveNightModeState(isChecked);
                // Restart the activity to apply the new theme
                recreate();
            }
        });
    }
}
