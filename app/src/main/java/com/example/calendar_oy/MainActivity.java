package com.example.calendar_oy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String INTENT_KEY = "page";
    private final int TABLE_CALENDAR_PAGE = 1;
    private final int SETTINGS_PAGE = 2;

    private CardView bucketCard;
    private CardView listCard;
    private CardView calendarCard;
    private CardView timelineCard;
    private CardView notesCard;
    private CardView settingsCard;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useSharedPreferences();

        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);
        initViews();
        registerListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.Theme_CalendarOYDark);
        } else {
            setTheme(R.style.Theme_CalendarOY);
        }
        recreate();
    }


    private void initViews() {
        bucketCard = findViewById(R.id.bucketCard);
        listCard = findViewById(R.id.listCard);
        calendarCard = findViewById(R.id.calendarCard);
        timelineCard = findViewById(R.id.timelineCard);
        notesCard = findViewById(R.id.notesCard);
        settingsCard = findViewById(R.id.settingsCard);
    }

    private void registerListeners() {
        bucketCard.setOnClickListener(this);
        listCard.setOnClickListener(this);
        calendarCard.setOnClickListener(this);
        timelineCard.setOnClickListener(this);
        notesCard.setOnClickListener(this);
        settingsCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.bucketCard:
            case R.id.listCard:
            case R.id.timelineCard:
            case R.id.notesCard:
                // handle these cases if needed
                break;

            case R.id.calendarCard:
                i = new Intent(this, TableCalendar.class);
                i.putExtra(INTENT_KEY, TABLE_CALENDAR_PAGE);
                startActivity(i);
                break;

            case R.id.settingsCard:
                i = new Intent(this, SettingsPage.class);
                i.putExtra(INTENT_KEY, SETTINGS_PAGE);
                startActivity(i);
                break;

            default:
                break;
        }
    }


    public void useSharedPreferences() {
        Log.d("isWorking", "sharedPref works");
        sharedPref = new SharedPref(getApplicationContext());
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.Theme_CalendarOYDark);
        } else {
            setTheme(R.style.Theme_CalendarOY);
        }
    }
}
