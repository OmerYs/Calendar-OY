package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TableCalendar extends AppCompatActivity {

    private TextView textView;
    private GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    private SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private ArrayList<Date> dates = new ArrayList<>();
    private ArrayList<Events> eventsList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_calendar);

        gridView = findViewById(R.id.gridView);
        textView = findViewById(R.id.currentDate);
        textView.setText(dateFormat.format(calendar.getTime()));

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });

        findViewById(R.id.previousButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TableCalendar.this, EventsActivity.class);
                intent.putExtra("date", eventDateFormat.format(dates.get(i)));
                startActivity(intent);
            }
        });

        SetUpCalendar();
        fetchEventsAndUpdateCalendar();
    }

    private void SetUpCalendar() {
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        String monthString = monthFormat.format(calendar.getTime());
        String yearString = yearFormat.format(calendar.getTime());
        textView.setText(monthString + " " + yearString);

        CustomCalendarAdapter customCalendarAdapter = new CustomCalendarAdapter(getApplicationContext(), dates, calendar, eventsList);
        gridView.setAdapter(customCalendarAdapter);
    }

    private void fetchEventsAndUpdateCalendar() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("events");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Events event = eventSnapshot.getValue(Events.class);
                    if (event != null) {
                        eventsList.add(event);
                        Log.d("Event Added", "Event: " + event.toString()); // Debug log statement
                    }
                }
                SetUpCalendar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(eventListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}


