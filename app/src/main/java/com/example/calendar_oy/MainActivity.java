package com.example.calendar_oy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView BucketCard, CalendarCard, TimelineCard, SettingCard;
    public SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        sharedPref.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BucketCard = findViewById(R.id.bucketCard);
        BucketCard.setOnClickListener(this);
        CalendarCard = findViewById(R.id.calendarCard);
        CalendarCard.setOnClickListener(this);
        TimelineCard = findViewById(R.id.timelineCard);
        TimelineCard.setOnClickListener(this);
        SettingCard = findViewById(R.id.settingsCard);
        SettingCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.bucketCard:
                i = new Intent(this, BucketList.class);
                finish();
                startActivity(i);
                break;
            case R.id.calendarCard:
                i = new Intent(this, TableCalendar.class);
                finish();
                startActivity(i);
                break;
            case R.id.timelineCard:
                i = new Intent(this, TimeLinePage.class);
                finish();
                startActivity(i);
                break;
            case R.id.settingsCard:
                i = new Intent(this, SettingsPage.class);
                finish();
                startActivity(i);
                break;
        }
    }

}
