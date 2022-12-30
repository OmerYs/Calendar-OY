package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView BucketCard, ListCard, CalendarCard, TimelineCard, NotesCard, SettingCard;
    public SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BucketCard = (CardView) findViewById(R.id.bucketCard);
        BucketCard.setOnClickListener((View.OnClickListener)this);
        ListCard = (CardView) findViewById(R.id.listCard);
        ListCard.setOnClickListener((View.OnClickListener)this);
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
            case R.id.bucketCard:;
            case R.id.listCard:;
            case R.id.calendarCard:;
            case R.id.timelineCard:;
            case R.id.notesCard:;
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