package com.example.calendar_oy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView BucketCard, CalendarCard, TimelineCard, NotesCard, SettingCard;
    public SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BucketCard = (CardView) findViewById(R.id.bucketCard);
        BucketCard.setOnClickListener((View.OnClickListener)this);
        CalendarCard = (CardView) findViewById(R.id.calendarCard);
        CalendarCard.setOnClickListener((View.OnClickListener)this);
        TimelineCard = (CardView) findViewById(R.id.timelineCard);
        TimelineCard.setOnClickListener((View.OnClickListener)this);
        NotesCard = (CardView) findViewById(R.id.notesCard);
        NotesCard.setOnClickListener((View.OnClickListener)this);
        SettingCard = (CardView) findViewById(R.id.settingsCard);
        SettingCard.setOnClickListener((View.OnClickListener)this);
    }

    @Override
    public void onClick(View view){
        Intent i;
        switch (view.getId()){
            case R.id.bucketCard: i = new Intent(this, BucketList.class);finish();startActivity(i);break;
            case R.id.calendarCard: i = new Intent(this, TableCalendar.class);finish();startActivity(i);break;
            case R.id.timelineCard: i = new Intent(this, TimeLinePage.class);finish();startActivity(i);break;
            case R.id.notesCard: i = new Intent(this, NotesPage.class);finish();startActivity(i);break;
            case R.id.settingsCard: i = new Intent(this, SettingsPage.class);finish();startActivity(i);break;
        }
    }

    public void useSharedPreferences(){
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()==true){
            setTheme(R.style.Theme_CalendarOYDark);
        }
        else{
            setTheme(R.style.Theme_CalendarOY);
        }
    }
}